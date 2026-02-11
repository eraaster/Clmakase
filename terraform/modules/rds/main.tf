################################################################################
# RDS Module - Aurora MySQL + ASM (Secrets Manager)
# CJ Oliveyoung CloudWave Infrastructure
#
# Private Subnet에 Aurora MySQL 클러스터 생성
# EKS 워커 노드에서만 접근 가능 (RDS SG)
# DB 비밀번호는 random_password로 자동 생성 → ASM + KMS로 안전하게 보관
################################################################################

locals {
  name_prefix = "${var.project_name}-${var.environment}"
}

# ------------------------------------------------------------------------------
# DB Subnet Group
# Aurora 클러스터를 배치할 서브넷 그룹 (Private Subnet 2개, Multi-AZ)
# ------------------------------------------------------------------------------
resource "aws_db_subnet_group" "this" {
  name       = "${local.name_prefix}-db-subnet-group"
  subnet_ids = var.private_subnet_ids

  tags = merge(var.common_tags, {
    Name = "${local.name_prefix}-db-subnet-group"
  })
}

# ------------------------------------------------------------------------------
# Random Password
# 16자리 랜덤 비밀번호 자동 생성 (terraform.tfvars에 평문 저장 제거)
# ------------------------------------------------------------------------------
resource "random_password" "db_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

# ------------------------------------------------------------------------------
# KMS Key
# ASM 비밀번호 암호화 전용 키 (자동 순환 활성화)
# ------------------------------------------------------------------------------
resource "aws_kms_key" "rds_secret_key" {
  description             = "KMS key for RDS password encryption"
  deletion_window_in_days = 7
  enable_key_rotation     = true

  tags = merge(var.common_tags, {
    Name = "${local.name_prefix}-rds-kms-key"
  })
}

# ------------------------------------------------------------------------------
# AWS Secrets Manager (ASM)
# DB 비밀번호를 KMS로 암호화하여 보관
# ------------------------------------------------------------------------------
resource "aws_secretsmanager_secret" "db_secret" {
  name       = "${local.name_prefix}/db-password"
  kms_key_id = aws_kms_key.rds_secret_key.arn

  tags = merge(var.common_tags, {
    Name = "${local.name_prefix}-db-secret"
  })
}

resource "aws_secretsmanager_secret_version" "db_password_val" {
  secret_id = aws_secretsmanager_secret.db_secret.id
  secret_string = jsonencode({
    username = var.master_username
    password = random_password.db_password.result
    host     = aws_rds_cluster.this.endpoint
    port     = 3306
    dbname   = var.database_name
  })
}

# ------------------------------------------------------------------------------
# Aurora MySQL Cluster
# ------------------------------------------------------------------------------
resource "aws_rds_cluster" "this" {
  cluster_identifier = "${local.name_prefix}-aurora"
  engine             = "aurora-mysql"
  engine_version     = "8.0.mysql_aurora.3.04.6"

  database_name   = var.database_name
  master_username = var.master_username
  master_password = random_password.db_password.result

  db_subnet_group_name   = aws_db_subnet_group.this.name
  vpc_security_group_ids = [var.rds_sg_id]

  # Dev 환경 설정
  skip_final_snapshot = true
  deletion_protection = false

  # 백업
  backup_retention_period = 1
  preferred_backup_window = "03:00-04:00"

  tags = merge(var.common_tags, {
    Name = "${local.name_prefix}-aurora-cluster"
  })
}

# ------------------------------------------------------------------------------
# Aurora MySQL Instance (Writer)
# ------------------------------------------------------------------------------
resource "aws_rds_cluster_instance" "writer" {
  identifier         = "${local.name_prefix}-aurora-writer"
  cluster_identifier = aws_rds_cluster.this.id
  instance_class     = var.instance_class
  engine             = aws_rds_cluster.this.engine
  engine_version     = aws_rds_cluster.this.engine_version

  db_subnet_group_name = aws_db_subnet_group.this.name

  tags = merge(var.common_tags, {
    Name = "${local.name_prefix}-aurora-writer"
  })
}
