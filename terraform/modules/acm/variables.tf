################################################################################
# ACM Module - Variables
################################################################################

variable "project_name" {
  description = "프로젝트 이름"
  type        = string
}

variable "domain_name" {
  description = "도메인 이름 (예: clmakase.click)"
  type        = string
}

variable "route53_zone_id" {
  description = "Route53 Hosted Zone ID (DNS 검증용)"
  type        = string
}

variable "common_tags" {
  description = "공통 태그"
  type        = map(string)
}
