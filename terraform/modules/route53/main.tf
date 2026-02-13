################################################################################
# Route53 Module
# Hosted Zone만 생성 (레코드는 root에서 직접 관리)
# → ACM DNS 검증에 zone_id가 먼저 필요하므로 분리
################################################################################

# Hosted Zone (clmakase.click)
resource "aws_route53_zone" "this" {
  name    = var.domain_name
  comment = "${var.project_name} DNS Zone"

  tags = merge(var.common_tags, {
    Name = "${var.project_name}-zone"
  })
}
