#pragma once

#include <stdint.h>

typedef  uint8_t  BYTE; 
typedef uint16_t  WORD; 
typedef uint32_t DWORD; 
typedef  int32_t  LONG; 

#define BMP_MAGIC (*(WORD*)"BM")
#define BI_RGB 0L

//
// Origin: https://docs.microsoft.com/en-us/windows/win32/api/wingdi/ns-wingdi-bitmapfileheader
//
typedef struct __attribute__((packed)) tagBITMAPFILEHEADER {
  WORD  bfType;
  DWORD bfSize;
  WORD  bfReserved1;
  WORD  bfReserved2;
  DWORD bfOffBits;
} BITMAPFILEHEADER;

//
// Origin: https://docs.microsoft.com/en-us/windows/win32/api/wingdi/ns-wingdi-bitmapinfoheader
//
typedef struct __attribute__((packed)) tagBITMAPINFOHEADER {
  DWORD biSize;
  LONG  biWidth;
  LONG  biHeight;
  WORD  biPlanes;
  WORD  biBitCount;
  DWORD biCompression;
  DWORD biSizeImage;
  LONG  biXPelsPerMeter;
  LONG  biYPelsPerMeter;
  DWORD biClrUsed;
  DWORD biClrImportant;
} BITMAPINFOHEADER;
	
//
// Origin: https://docs.microsoft.com/en-us/windows/win32/api/wingdi/ns-wingdi-rgbtriple
//
typedef struct __attribute__((packed)) tagRGBTRIPLE {
  BYTE rgbtBlue;
  BYTE rgbtGreen;
  BYTE rgbtRed;
} RGBTRIPLE;
