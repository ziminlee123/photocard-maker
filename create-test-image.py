from PIL import Image, ImageDraw, ImageFont
import os

def create_test_image():
    # 300x400 크기의 이미지 생성 (포토카드 크기)
    width, height = 300, 400
    image = Image.new('RGB', (width, height), color='white')
    draw = ImageDraw.Draw(image)
    
    # 테스트 텍스트 추가
    try:
        # 기본 폰트 사용
        font = ImageFont.load_default()
    except:
        font = None
    
    # 제목
    draw.text((50, 50), "TEST PHOTOCARD", fill='black', font=font)
    
    # 내용
    draw.text((50, 100), "This is a test image", fill='gray', font=font)
    draw.text((50, 120), "for download_link testing", fill='gray', font=font)
    
    # 테두리 그리기
    draw.rectangle([10, 10, width-10, height-10], outline='black', width=2)
    
    # 이미지 저장
    image.save('test-photocard.jpg', 'JPEG')
    print("테스트 이미지 생성 완료: test-photocard.jpg")

if __name__ == "__main__":
    create_test_image()
