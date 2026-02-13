################################################################################
# WAF Module - Variables
################################################################################

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "cloudwave"
}

variable "environment" {
  description = "Environment"
  type        = string
  default     = "dev"
}

variable "common_tags" {
  description = "Common tags"
  type        = map(string)
  default     = {}
}
