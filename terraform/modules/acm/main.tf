################################################################################
# ACM Module
# CloudFront용 SSL 인증서 (us-east-1 필수)
# DNS 검증 방식 → Route53에서 자동 검증
################################################################################

# 1. ACM 인증서 요청
resource "aws_acm_certificate" "this" {
  domain_name               = var.domain_name
  subject_alternative_names = ["*.${var.domain_name}"]
  validation_method         = "DNS"

  tags = merge(var.common_tags, {
    Name = "${var.project_name}-acm"
  })

  lifecycle {
    create_before_destroy = true
  }
}

# 2. Route53에 DNS 검증 레코드 생성
resource "aws_route53_record" "acm_validation" {
  for_each = {
    for dvo in aws_acm_certificate.this.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = var.route53_zone_id
}

# 3. 인증서 검증 완료 대기
resource "aws_acm_certificate_validation" "this" {
  certificate_arn         = aws_acm_certificate.this.arn
  validation_record_fqdns = [for record in aws_route53_record.acm_validation : record.fqdn]
}
