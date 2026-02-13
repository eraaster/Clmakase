################################################################################
# KMS Module - Variables
################################################################################

variable "project_name" {
  description = "프로젝트 이름"
  type        = string
}

variable "description" {
  description = "KMS 키 설명"
  type        = string
  default     = "KMS key for Clmakase S3 Assets"
}

variable "key_alias" {
  description = "KMS 키의 별칭 (예: clmakase-s3-key)"
  type        = string
}

variable "common_tags" {
  description = "공통 태그"
  type        = map(string)
}
