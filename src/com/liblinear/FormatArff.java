package com.liblinear;

/*
 * 说明：由原始单纯的正反例文件生成特定数量的训练集，其余序列为测试集。
 * 作者：万世想
 * 时间：2015-10-05
 * */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import javax.swing.JOptionPane;

public class FormatArff {
	public static void main(String[] args) {
		String file = "feature.arff"; //file
		new FormatArff().run(file);
	}
	
	@SuppressWarnings("resource")
	public void run(String file) {
		System.out.println("...generating train&test by "+file);
		String br_line1, br_line2;
		int all = 0, error = 0;
		try {
			//Please input the fasta file.
			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedReader br_main = new BufferedReader(new FileReader(file));
			//1. Count the number of pos, neg and error.
			while ((br_line1 = br.readLine()) != null) {
				if (br_line1.trim().equals("")) continue;
				if (br_line1.substring(0, 1).matches("[0-9]")) {
					all++;
				}
			}
			System.out.println("seq numbers: " + all);
			if (error != 0) System.out.println("Invalid numbers: " + error); 
			
			String input_num = JOptionPane.showInputDialog("enter a seq number you want in train.arff (<"+all+"):");
			System.out.println("seq numbers in train: "+input_num);
			int train_num = Integer.valueOf(input_num);
			if (train_num > all || train_num < 0) {
				System.out.println("Invalid input!");
				return;
			}
			//2. Choose random instance of all.
			Random random = new Random();
			boolean r_train[] = new boolean[all];
			for (int i = 0; i < train_num;) {
				int temp_train = random.nextInt(all);
				if (!r_train[temp_train]) {
					r_train[temp_train] = true;
					i++;
				}
			}
			//3. Integrate the pos and neg.
			BufferedWriter bw_main = new BufferedWriter(new FileWriter("train_" + input_num + ".arff"));
			BufferedWriter bw_re = new BufferedWriter(new FileWriter("test_" + input_num + ".arff"));
			int i = 0;
			while (br_main.ready()) {
				br_line2 = br_main.readLine();
				if (br_line2.trim().equals("")) continue;
				if (br_line2 != null) {
					if (!br_line2.substring(0, 1).matches("[0-9]")) {
						bw_main.write(br_line2 + "\n");
						bw_re.write(br_line2 + "\n");
					} else {
						if (r_train[i] && i < all) {
							bw_main.write(br_line2 + "\n");
						} else {
							bw_re.write(br_line2 + "\n");
						}
						i++;
					}
				}
			}
			br.close();
			br_main.close();
			bw_main.close();
			bw_re.close();
			System.out.println("FormatArff OK!");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
}

