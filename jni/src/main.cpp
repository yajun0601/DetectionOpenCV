#include "include/plate_recognize.h"
#include "include/util.h"
#include "include/features.h"

using namespace easypr;

int svmMain();
int acurayTestMain();

namespace easypr {

	int svmTrain(bool dividePrepared = true, bool trainPrepared = true,
	svmCallback getFeatures = getHistogramFeatures);

}

extern const string GENERAL_TEST_PATH = "image/general_test";
extern const string NATIVE_TEST_PATH = "image/native_test";

////////////////////////////////////////////////////////////
// EasyPR ѵ��������

const string option[] = 
	{
		"1. ����;"	,
		"2. ��������;"		,
		"3. SVMѵ��;"		,
		"4. ANNѵ��(δ����);"		,
		"5. GDTS����;"		,
		"6. �����Ŷ�;"		,
		"7. ��л����;"		,
		"8. �˳�;"			,  
	};

const int optionCount = 8;

int main()
{
	bool isExit = false;
	while (isExit != true)
	{
		stringstream selectOption(stringstream::in | stringstream::out);
		selectOption << "EasyPR Option:"<< endl;
		for(int i = 0; i < optionCount; i++)
		{
			selectOption << option[i] << endl;
		}

		cout << "////////////////////////////////////"<< endl;
		cout << selectOption.str();
		cout << "////////////////////////////////////"<< endl;
		cout << "��ѡ��һ�����:";

		int select = -1;
		bool isRepeat = true;
		while (isRepeat)
		{
			cin >> select;
			isRepeat = false;
			switch (select)
			{
			case 1:
				testMain();
				break;
			case 2:
				acurayTestMain();
				break;
			case 3:
				svmMain();
				break;
			case 4:
				// TODO
				break;
			case 5:
				generate_gdts();
				break;
			case 6:
				// �����Ŷ�;
				cout << endl;
				cout << "����EasyPR�Ŷ�Ŀǰ��һ��5�����ҵ�С���ڽ���EasyPR�����汾�Ŀ���������" << endl;
				cout << "�����Ա���Ŀ����Ȥ������Ը��Ϊ��Դ����һ�����������Ǻܻ�ӭ��ļ��롣" << endl;
				cout << "Ŀǰ��Ƹ����Ҫ�˲��ǣ����ƶ�λ��ͼ��ʶ�����ѧϰ����վ������ط����ţ�ˡ�" << endl;
				cout << "���������Լ������������뷢�ʼ�����ַ(easypr_dev@163.com)���ڴ���ļ��룡" << endl;
				cout << endl;
				break;
			case 7:
				// ��л����
				cout << endl;
				cout << "����Ŀ�ڽ�������У��ܵ��˺ܶ��˵İ��������������ǶԱ���Ŀ����ͻ�����׵�" << endl;
				cout << "(���װ������潨�飬������ţ������ṩ�ȵ�,������ʱ��˳��)��" << endl;
				cout << "taotao1233, �ƴ�����jsxyhelu�������һ�죬ѧϰ�ܶ���Ԭ��־��ʥ��Сʯ����" << endl;
				cout << "���кܶ��ͬѧ�Ա���ĿҲ�����˹�����֧�֣��ڴ�Ҳһ����ʾ��ϵ�л�⣡" << endl;
				cout << endl;
				break;
			case 8:
				isExit = true;
				break;
			default:
				cout << "�����������������:";
				isRepeat = true;
				break;
			}
		}
	}
	return 0;
}
// /EasyPR ѵ�������� ����
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// acurayTestMain ������ 

const string acuray_option[] = 
	{
		"1. general_test;"	,
		"2. native_test;"	,
		"3. ����;"			,  
	};

const int acuray_optionCount = 3;

int acurayTestMain()
{
	bool isExit = false;
	while (isExit != true)
	{
		stringstream selectOption(stringstream::in | stringstream::out);
		selectOption << "BatchTest Option:"<< endl;
		for(int i = 0; i < acuray_optionCount; i++)
		{
			selectOption << acuray_option[i] << endl;
		}

		cout << "////////////////////////////////////"<< endl;
		cout << selectOption.str();
		cout << "////////////////////////////////////"<< endl;
		cout << "��ѡ��һ�����:";

		int select = -1;
		bool isRepeat = true;
		while (isRepeat)
		{
			cin >> select;
			isRepeat = false;
			switch (select)
			{
			case 1:
				acurayTest(GENERAL_TEST_PATH);
				break;
			case 2:
				acurayTest(NATIVE_TEST_PATH);
				break;
			case 3:
				isExit = true;
				break;
			default:
				cout << "�����������������:";
				isRepeat = true;
				break;
			}
		}
	}
	return 0;
}

// acurayTestMain ������ 
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// SVM ѵ�������� 

const string svm_option[] = 
	{
		"1. ����learndata(�������뵽��Ļ���������);"	,
		"2. ��ǩlearndata;"		,
		"3. ���Ƽ��(not divide and train);"		,
		"4. ���Ƽ��(not train);"		,
		"5. ���Ƽ��(not divide);"		,
		"6. ���Ƽ��;"		,
		"7. ����;"			,  
	};

const int svm_optionCount = 7;

int svmMain()
{
	bool isExit = false;
	while (isExit != true)
	{
		stringstream selectOption(stringstream::in | stringstream::out);
		selectOption << "SvmTrain Option:"<< endl;
		for(int i = 0; i < svm_optionCount; i++)
		{
			selectOption << svm_option[i] << endl;
		}

		cout << "////////////////////////////////////"<< endl;
		cout << selectOption.str();
		cout << "////////////////////////////////////"<< endl;
		cout << "��ѡ��һ�����:";

		int select = -1;
		bool isRepeat = true;
		while (isRepeat)
		{
			cin >> select;
			isRepeat = false;
			switch (select)
			{
			case 1:
				getLearnData();
				break;
			case 2:
				label_data();
				break;
			case 3:
				svmTrain(false, false);
				break;
			case 4:
				svmTrain(true, false);
				break;
			case 5:
				svmTrain(false, true);
				break;
			case 6:
				svmTrain();
				break;
			case 7:
				isExit = true;
				break;
			default:
				cout << "�����������������:";
				isRepeat = true;
				break;
			}
		}
	}
	return 0;
}

// SVM ѵ�������� 
////////////////////////////////////////////////////////////