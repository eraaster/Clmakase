################################################################################
# ACM Module - Outputs
################################################################################

output "certificate_arn" {
  description = "ACM 인증서 ARN (CloudFront 연동용)"
  value       = aws_acm_certificate_validation.this.certificate_arn
}

output "certificate_domain" {
  description = "인증서 도메인"
  value       = aws_acm_certificate.this.domain_name
}
