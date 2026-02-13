################################################################################
# S3 Module
# 정적 자산 버킷 (CloudFront OAC 전용 - 퍼블릭 차단)
################################################################################

# 1. S3 버킷
resource "aws_s3_bucket" "this" {
  bucket = "${var.project_name}-assets-${var.environment}"

  tags = merge(var.common_tags, {
    Name = "${var.project_name}-assets"
  })
}

# 2. 버전 관리
resource "aws_s3_bucket_versioning" "this" {
  bucket = aws_s3_bucket.this.id

  versioning_configuration {
    status = "Enabled"
  }
}

# 3. 퍼블릭 액세스 완전 차단 (OAC만 허용)
resource "aws_s3_bucket_public_access_block" "this" {
  bucket = aws_s3_bucket.this.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# 4. 서버 측 암호화 (SSE-KMS)
resource "aws_s3_bucket_server_side_encryption_configuration" "this" {
  bucket = aws_s3_bucket.this.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = var.kms_key_arn
    }
    bucket_key_enabled = true
  }
}
