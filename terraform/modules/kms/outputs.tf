################################################################################
# KMS Module - Outputs
################################################################################

output "key_arn" {
  description = "KMS 키 ARN (S3 암호화 설정용)"
  value       = aws_kms_key.this.arn
}

output "key_id" {
  description = "KMS 키 ID"
  value       = aws_kms_key.this.key_id
}
