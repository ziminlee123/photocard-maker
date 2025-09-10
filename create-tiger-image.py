from PIL import Image, ImageDraw, ImageFont
import os

def create_tiger_artwork():
    # 600x800 크기의 이미지 생성 (세로형 포토카드)
    width, height = 600, 800
    image = Image.new('RGB', (width, height), color='#F5F5DC')  # 베이지색 배경
    draw = ImageDraw.Draw(image)
    
    try:
        # 기본 폰트 사용
        font_large = ImageFont.load_default()
        font_medium = ImageFont.load_default()
    except:
        font_large = None
        font_medium = None
    
    # 배경에 종이 질감 효과 (점으로 표현)
    for i in range(0, width, 3):
        for j in range(0, height, 3):
            if (i + j) % 6 == 0:
                draw.point((i, j), fill='#E8E8E8')
    
    # 소나무 그리기 (왼쪽)
    draw.line([(50, 100), (50, 700)], fill='#8B4513', width=15)  # 줄기
    draw.line([(30, 200), (100, 180)], fill='#228B22', width=8)  # 가지
    draw.line([(25, 300), (90, 280)], fill='#228B22', width=8)  # 가지
    draw.line([(30, 400), (95, 380)], fill='#228B22', width=8)  # 가지
    
    # 소나무 잎 (원형으로)
    for x, y in [(80, 180), (85, 200), (75, 190), (90, 210)]:
        draw.ellipse([x-8, y-8, x+8, y+8], fill='#228B22')
    
    # 호랑이 그리기 (중앙 하단)
    # 호랑이 몸통
    draw.ellipse([200, 500, 400, 650], fill='#D3D3D3', outline='#000000', width=2)
    
    # 호랑이 머리
    draw.ellipse([250, 450, 350, 550], fill='#D3D3D3', outline='#000000', width=2)
    
    # 호랑이 귀
    draw.polygon([(280, 450), (290, 430), (300, 450)], fill='#D3D3D3', outline='#000000')
    draw.polygon([(300, 450), (310, 430), (320, 450)], fill='#D3D3D3', outline='#000000')
    
    # 호랑이 눈
    draw.ellipse([270, 480, 290, 500], fill='#FFD700', outline='#000000', width=2)
    draw.ellipse([310, 480, 330, 500], fill='#FFD700', outline='#000000', width=2)
    draw.ellipse([275, 485, 285, 495], fill='#000000')  # 눈동자
    draw.ellipse([315, 485, 325, 495], fill='#000000')  # 눈동자
    
    # 호랑이 코
    draw.polygon([(295, 520), (300, 530), (305, 520)], fill='#FF0000')
    
    # 호랑이 입
    draw.arc([290, 530, 310, 550], 0, 180, fill='#000000', width=3)
    
    # 호랑이 줄무늬
    for i in range(5):
        y = 480 + i * 20
        draw.line([(220, y), (380, y)], fill='#000000', width=3)
    
    # 호랑이 꼬리
    draw.arc([380, 550, 480, 650], 0, 90, fill='#D3D3D3', outline='#000000', width=3)
    
    # 까치 그리기 (소나무 위)
    # 첫 번째 까치 (앉아있는)
    draw.ellipse([80, 200, 120, 240], fill='#000000')  # 몸통
    draw.ellipse([100, 180, 140, 220], fill='#FFFFFF')  # 배
    draw.ellipse([110, 190, 130, 210], fill='#000000')  # 머리
    draw.polygon([(125, 200), (135, 195), (130, 205)], fill='#000000')  # 부리
    
    # 두 번째 까치 (날아오르는)
    draw.ellipse([300, 300, 340, 340], fill='#000000')  # 몸통
    draw.ellipse([320, 280, 360, 320], fill='#FFFFFF')  # 배
    draw.ellipse([330, 290, 350, 310], fill='#000000')  # 머리
    draw.polygon([(345, 300), (355, 295), (350, 305)], fill='#000000')  # 부리
    
    # 바위 그리기 (호랑이 아래)
    draw.polygon([(180, 650), (220, 680), (200, 700), (160, 680)], fill='#696969', outline='#000000')
    draw.polygon([(200, 680), (240, 710), (220, 730), (180, 710)], fill='#696969', outline='#000000')
    
    # 제목 추가
    draw.text((200, 50), "호작도 (虎鵲圖)", fill='#8B4513', font=font_large)
    draw.text((220, 80), "Tiger and Magpie", fill='#8B4513', font=font_medium)
    
    # 이미지 저장
    image.save('tiger-artwork.jpg', 'JPEG', quality=95)
    print("호작도 이미지 생성 완료: tiger-artwork.jpg")
    print(f"이미지 크기: {width}x{height}")

if __name__ == "__main__":
    create_tiger_artwork()
