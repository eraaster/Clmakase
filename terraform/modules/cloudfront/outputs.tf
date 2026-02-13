################################################################################
# CloudFront Module - Outputs
################################################################################

output "cloudfront_domain_name" {
  description = "CloudFront 배포 도메인 (Route53 연동용)"
  value       = aws_cloudfront_distribution.this.domain_name
}

output "cloudfront_arn" {
  description = "CloudFront 배포 ARN"
  value       = aws_cloudfront_distribution.this.arn
}

output "cloudfront_distribution_id" {
  description = "CloudFront Distribution ID"
  value       = aws_cloudfront_distribution.this.id
}

output "cloudfront_hosted_zone_id" {
  description = "CloudFront Hosted Zone ID (Route53 alias용)"
  value       = aws_cloudfront_distribution.this.hosted_zone_id
}
