################################################################################
# WAF Module - Outputs
################################################################################

output "web_acl_arn" {
  description = "WAF Web ACL ARN (CloudFront 연동용)"
  value       = aws_wafv2_web_acl.main.arn
}

output "web_acl_id" {
  description = "WAF Web ACL ID"
  value       = aws_wafv2_web_acl.main.id
}
