package com.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import com.liblinear.CalNum;
import com.liblinear.FormatArff;
import com.liblinear.Liblinear;

public class TrainMode {
	public static void main(String args[]) {
		String T = "main.arff remain.arff 10 2";
		args = T.split(" ");
		
		if (args.length == 1) {
			new FormatArff().run(args[0]);
			return;
		}
		String train_file = args[0]; // arff训练集文件名称
		String test_file = args[1]; // arff测试集文件名称
		int init_num = Integer.parseInt(args[2]); // 特征选择初始值
		int gap = Integer.parseInt(args[3]); //特征选择间隔
		String path = ""; // arff文件所在路径
		
		int disFunc = 1; //MRMD参数
		int lableNum = 1; //MRMD参数
		int bestfea_num = 0;
		double bestAccuracy = 0.0;

		String train_input = path + train_file;
		String test_input = path + test_file;
		int exam_num = new CalNum().getInstanceNum(train_input); // 训练集样本数
		int fea_num = new CalNum().getFeatureNum(train_input); // 样本总特征
		
		try {
			//从特征初始值开始，一次增加10个特征值实验
			for (int i = init_num; init_num <= i && i <= fea_num; i = i * gap)
			{
				System.out.print("Feature dimension：" + i);
				int selefea_num = i;
				
				// 调用MRMD.jar运行降维
				String TrainOutputFile = path + train_file + "_temp" + ".txt";
				String command = "java -jar "+path+"MRMD.jar -i " + train_input
						+ " -o " + TrainOutputFile + " -in " + exam_num
						+ " -fn " + fea_num + " -sn " + selefea_num + " -ln "
						+ lableNum + " -df " + disFunc + " -a " + path
						+ train_file + "_temp" + ".arff";
				Process processTrain = Runtime.getRuntime().exec(command);
				processTrain.waitFor();
				
				//需要进行训练与测试的文件
				String trainFile = path + train_file + "_temp" + ".arff";
				String testFile = path + test_file + "_temp" + ".arff";
				//将特征选择Train文件中的@语句，全部存入特征选择Test文件
				BufferedReader brarff = new BufferedReader(new FileReader(trainFile));
				BufferedWriter bw = new BufferedWriter(new FileWriter(testFile));
				String lString = null;
				while (brarff.ready()) {
					lString = brarff.readLine();
					if (lString.contains("data"))
						break;
					else {
						bw.write(lString);
						bw.newLine();
						bw.flush();
					}
				}
				bw.write(lString);
				bw.newLine();
				bw.flush();

				//从特征文件txt中提取特征标号存入整型数组feaRanked[]
				BufferedReader br = new BufferedReader(new FileReader(TrainOutputFile));
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
				String lString2 = null;
				String[] lString2Split = new String[3];
				int[] feaRanked = new int[selefea_num];
				int flag = 0;
				while (br.ready()) {
					lString2 = br.readLine();
					lString2Split = lString2.split("		");
					feaRanked[flag] = Integer.parseInt(lString2Split[1].substring(3, lString2Split[1].length())) + 1;
					flag++;
				}
				br.close();

				//依据上面得到的特征标号选取原始输入测试集，继续写入特征选择Test文件
				BufferedReader br2 = new BufferedReader(new FileReader(test_input));
				String[] fea2split = new String[fea_num + 1];
				String lString3 = null;
				while (br2.ready()) {
					lString3 = br2.readLine();
					if (!lString3.contains("@") && lString3 != null) {
						fea2split = lString3.split(",");
						for (int j = 0; j < selefea_num; j++) {
							bw.write(fea2split[feaRanked[j] - 1] + ",");
						}
						bw.write(fea2split[fea_num]);
						bw.newLine();
						bw.flush();
					}
				}
				br2.close();
				bw.close();
				brarff.close();

				System.out.println("OK");
				//Run Liblinear. Input：train, test, save path; output: F-score, temp file
				double accuracy = new Liblinear().getLiblinear(path, trainFile, testFile);
				
				//Save Best Output
				String flagMark;
				if (bestAccuracy < accuracy) {
					bestAccuracy = accuracy;
					bestfea_num = selefea_num;
					
					new TrainMode().saveBest(
							trainFile, path + train_file + "_best" + ".arff");
					new TrainMode().saveBest(
							testFile, path + test_file + "_best" + ".arff");
					new TrainMode().saveBest(
							path + "OutLiblinear_temp"+ ".txt", path + "OutLiblinear_best"+ ".txt");
					
					flagMark = "↑";
					System.out.print("F-Score：" + accuracy+"	");
					System.out.println(flagMark);
				} else if (bestAccuracy == accuracy) {
					flagMark = "=";
					i = i / gap - 5;
					System.out.print("F-Score：" + accuracy+"	");
					System.out.println(flagMark);
				} else {
					flagMark = "↓";
					System.out.print("F-Score：" + accuracy +"	");
					System.out.println(flagMark);
					break;
				}
				
				
				
			}//End of One Process
			
			System.out.println("\n" + "Best F-Score：" + bestAccuracy);
			System.out.println("Best feature dimension：" + bestfea_num);
			
		} catch (Exception ex) {
			System.out.println(ex);
			System.exit(0);
		}
				
	}
	
	public void saveBest(String currentFile, String bestFile) {
		try {
			String temp;
			BufferedReader br = new BufferedReader(new FileReader(currentFile));
			BufferedWriter bf = new BufferedWriter(new FileWriter(bestFile));
			temp = br.readLine();
			bf.write(temp);
			while (br.ready()) {
				temp = br.readLine();
				bf.write("\n" + temp);
			}
			br.close();
			bf.close();
		} catch (Exception e) {
			System.out.println("ERROR!");
		}
	}
	
}
