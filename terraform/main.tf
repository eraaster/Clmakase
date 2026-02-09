################################################################################
# Root Module - Main
# CJ Oliveyoung CloudWave Infrastructure
#
# Phase 1: VPC + Security Groups + ECR
# Phase 2: EKS (AWS Console에서 수동 생성)!
################################################################################

terraform {
  required_version = ">= 1.5"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

locals {
  common_tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

# ------------------------------------------------------------------------------
# VPC Module
# ------------------------------------------------------------------------------
module "vpc" {
  source = "./modules/vpc"

  project_name         = var.project_name
  environment          = var.environment
  vpc_cidr             = var.vpc_cidr
  azs                  = var.azs
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
  cluster_name         = var.cluster_name
  common_tags          = local.common_tags
}

# ------------------------------------------------------------------------------
# Security Groups Module
# ------------------------------------------------------------------------------
module "security_groups" {
  source = "./modules/security-groups"

  project_name = var.project_name
  environment  = var.environment
  vpc_id       = module.vpc.vpc_id
  vpc_cidr     = module.vpc.vpc_cidr
  common_tags  = local.common_tags
}

# ------------------------------------------------------------------------------
# ECR Module
# ------------------------------------------------------------------------------
module "ecr" {
  source = "./modules/ecr"

  project_name        = var.project_name
  environment         = var.environment
  repository_name     = var.ecr_repository_name
  image_count_to_keep = 10
  common_tags         = local.common_tags
}
