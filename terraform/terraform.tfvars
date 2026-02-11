################################################################################
# Terraform Variables - Dev Environment
# CJ Oliveyoung CloudWave
################################################################################

aws_region   = "ap-northeast-2"
project_name = "cloudwave"
environment  = "dev"

# VPC
vpc_cidr = "10.0.0.0/16"
azs      = ["ap-northeast-2a", "ap-northeast-2c"]

public_subnet_cidrs  = ["10.0.101.0/24", "10.0.102.0/24"]
private_subnet_cidrs = ["10.0.1.0/24", "10.0.2.0/24"]

# EKS
cluster_name = "cloudwave-eks"

# ECR
ecr_repository_name = "oliveyoung-api"

# RDS (Aurora MySQL)
# db_password 제거 - random_password 자동 생성 → ASM에 보관
