//////////////////////////////////////////////////////////////////////////
// Name:	    plate_locate Header
// Version:		1.0
// Date:	    2014-09-19
// MDate:		2014-09-29
// Author:	    liuruoze
// Copyright:   liuruoze
// Reference:	Mastering OpenCV with Practical Computer Vision Projects
// Reference:	CSDN Bloger taotao1233
// Desciption:  
// Defines CPlateLocate
//////////////////////////////////////////////////////////////////////////
#ifndef __PLATE_LOCATE_H__
#define __PLATE_LOCATE_H__

#include "prep.h"

/*! \namespace easypr
    Namespace where all the C++ EasyPR functionality resides
*/
namespace easypr {

class CPlateLocate 
{
public:
	CPlateLocate();

	//! ���ƶ�λ
	int plateLocate(Mat, vector<Mat>& );

	//! ���Ƶĳߴ���֤
	bool verifySizes(RotatedRect mr);

	//! ���������ʾ
	Mat showResultMat(Mat src, Size rect_size, Point2f center, int index);

	//! ����ģʽ�빤ҵģʽ�л�
	void setLifemode(bool param);

	//! �������ȡ����
	inline void setGaussianBlurSize(int param){ m_GaussianBlurSize = param;}
	inline int getGaussianBlurSize() const{ return m_GaussianBlurSize;}

	inline void setMorphSizeWidth(int param){ m_MorphSizeWidth = param;}
	inline int getMorphSizeWidth() const{ return m_MorphSizeWidth;}

	inline void setMorphSizeHeight(int param){ m_MorphSizeHeight = param;}
	inline int getMorphSizeHeight() const{ return m_MorphSizeHeight;}

	inline void setVerifyError(float param){ m_error = param;}
	inline float getVerifyError() const { return m_error;}
	inline void setVerifyAspect(float param){ m_aspect = param;}
	inline float getVerifyAspect() const { return m_aspect;}

	inline void setVerifyMin(int param){ m_verifyMin = param;}
	inline void setVerifyMax(int param){ m_verifyMax = param;}

	inline void setJudgeAngle(int param){ m_angle = param;}

	//! �Ƿ�������ģʽ
	inline void setDebug(int param){ m_debug = param;}

	//! ��ȡ����ģʽ״̬
	inline int getDebug(){ return m_debug;}

	//! PlateLocate���ó���
	static const int DEFAULT_GAUSSIANBLUR_SIZE = 5;
	static const int SOBEL_SCALE = 1;
	static const int SOBEL_DELTA = 0;
	static const int SOBEL_DDEPTH = CV_16S;
	static const int SOBEL_X_WEIGHT = 1;
	static const int SOBEL_Y_WEIGHT = 0 ;
	static const int DEFAULT_MORPH_SIZE_WIDTH = 17;
	static const int DEFAULT_MORPH_SIZE_HEIGHT = 3;

	//! showResultMat���ó���
	static const int WIDTH = 136;
	static const int HEIGHT = 36;
	static const int TYPE = CV_8UC3;
	
	//! verifySize���ó���
	static const int DEFAULT_VERIFY_MIN = 3;
	static const int DEFAULT_VERIFY_MAX = 20;

	//! �Ƕ��ж����ó���
	static const int DEFAULT_ANGLE = 30;

	//! �Ƿ�������ģʽ������Ĭ��0����ر�
	static const int DEFAULT_DEBUG = 0;

protected:
	//! ��˹ģ�����ñ���
	int m_GaussianBlurSize;

	//! ���Ӳ������ñ���
	int m_MorphSizeWidth;
	int m_MorphSizeHeight;

	//! verifySize���ñ���
	float m_error;
	float m_aspect;
	int m_verifyMin;
	int m_verifyMax;

	//! �Ƕ��ж����ñ���
	int m_angle;

	//! �Ƿ�������ģʽ��0�رգ���0����
	int m_debug;
};

}	/*! \namespace easypr*/

#endif /* endif __PLATE_LOCATE_H__ */