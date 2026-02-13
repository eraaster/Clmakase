################################################################################
# S3 Module - Outputs
################################################################################

output "bucket_id" {
  description = "S3 버킷 ID"
  value       = aws_s3_bucket.this.id
}

output "bucket_arn" {
  description = "S3 버킷 ARN"
  value       = aws_s3_bucket.this.arn
}

output "bucket_regional_domain_name" {
  description = "S3 버킷 Regional Domain Name (CloudFront 오리진용)"
  value       = aws_s3_bucket.this.bucket_regional_domain_name
}
