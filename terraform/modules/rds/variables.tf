################################################################################
# RDS Module - Variables
################################################################################

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "cloudwave"
}

variable "environment" {
  description = "Environment"
  type        = string
  default     = "dev"
}

variable "private_subnet_ids" {
  description = "Private subnet IDs for DB subnet group"
  type        = list(string)
}

variable "rds_sg_id" {
  description = "RDS security group ID"
  type        = string
}

variable "database_name" {
  description = "Database name"
  type        = string
  default     = "oliveyoung"
}

variable "master_username" {
  description = "Master username"
  type        = string
  default     = "admin"
}

variable "master_password" {
  description = "Master password"
  type        = string
  sensitive   = true
}

variable "instance_class" {
  description = "Aurora instance class"
  type        = string
  default     = "db.t3.medium"
}

variable "common_tags" {
  description = "Common tags"
  type        = map(string)
  default     = {}
}
