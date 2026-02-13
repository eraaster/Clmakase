################################################################################
# S3 Module - Variables
################################################################################

variable "project_name" {
  description = "프로젝트 이름"
  type        = string
}

variable "environment" {
  description = "환경 (dev/prod)"
  type        = string
}

variable "common_tags" {
  description = "공통 태그"
  type        = map(string)
}

variable "kms_key_arn" {
  description = "S3 암호화용 KMS 키 ARN"
  type        = string
}
