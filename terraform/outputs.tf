################################################################################
# Root Module - Outputs
# AWS Console에서 EKS 생성 시 필요한 값들
################################################################################

# --- VPC ---
output "vpc_id" {
  description = "VPC ID"
  value       = module.vpc.vpc_id
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value       = module.vpc.public_subnet_ids
}

output "private_subnet_ids" {
  description = "Private subnet IDs (EKS 노드 배치)"
  value       = module.vpc.private_subnet_ids
}

# --- Security Groups ---
output "eks_control_plane_sg_id" {
  description = "EKS Control Plane SG ID"
  value       = module.security_groups.eks_control_plane_sg_id
}

output "eks_node_sg_id" {
  description = "EKS Node SG ID"
  value       = module.security_groups.eks_node_sg_id
}

output "alb_sg_id" {
  description = "ALB SG ID"
  value       = module.security_groups.alb_sg_id
}

output "rds_sg_id" {
  description = "RDS SG ID"
  value       = module.security_groups.rds_sg_id
}

output "redis_sg_id" {
  description = "Redis SG ID"
  value       = module.security_groups.redis_sg_id
}

# --- ECR ---
output "ecr_repository_url" {
  description = "ECR repository URL"
  value       = module.ecr.repository_url
}

output "ecr_repository_name" {
  description = "ECR repository name"
  value       = module.ecr.repository_name
}

# --- RDS (Aurora MySQL) ---
output "rds_cluster_endpoint" {
  description = "Aurora MySQL Writer Endpoint"
  value       = module.rds.cluster_endpoint
}

output "rds_reader_endpoint" {
  description = "Aurora MySQL Reader Endpoint"
  value       = module.rds.reader_endpoint
}

output "rds_database_name" {
  description = "Database name"
  value       = module.rds.database_name
}

# --- EKS 콘솔 생성 시 필요한 정보 요약 ---
output "eks_console_info" {
  description = "AWS Console에서 EKS 생성 시 필요한 정보"
  value = {
    vpc_id               = module.vpc.vpc_id
    private_subnet_ids   = module.vpc.private_subnet_ids
    public_subnet_ids    = module.vpc.public_subnet_ids
    control_plane_sg_id  = module.security_groups.eks_control_plane_sg_id
    node_sg_id           = module.security_groups.eks_node_sg_id
  }
}
