################################################################################
# RDS Module - Outputs
################################################################################

output "cluster_endpoint" {
  description = "Aurora cluster writer endpoint"
  value       = aws_rds_cluster.this.endpoint
}

output "reader_endpoint" {
  description = "Aurora cluster reader endpoint"
  value       = aws_rds_cluster.this.reader_endpoint
}

output "database_name" {
  description = "Database name"
  value       = aws_rds_cluster.this.database_name
}

output "port" {
  description = "Database port"
  value       = aws_rds_cluster.this.port
}

output "cluster_identifier" {
  description = "Aurora cluster identifier"
  value       = aws_rds_cluster.this.cluster_identifier
}

# --- ASM (Secrets Manager) ---
output "db_secret_arn" {
  description = "The ARN of the Secrets Manager secret"
  value       = aws_secretsmanager_secret.db_secret.arn
}

output "db_secret_name" {
  description = "The Name of the Secrets Manager secret"
  value       = aws_secretsmanager_secret.db_secret.name
}
