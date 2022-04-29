import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class AprioriAlgorithm {

	public static int k = 0;// 迭代次数
	private static double kMinSupport = 0.02;// 最小支持度百分比
	private static double kMinConfidence = 0.6;// 最小置信度
	private static boolean endTag = false;// 循环状态，迭代标识
	static List<List<String>> transaction = new ArrayList<List<String>>();// 数据集
	static List<List<String>> frequent_itemsets = new ArrayList<>();// 存储所有的频繁项集
	static List<Map> map = new ArrayList();// 存放频繁项集和对应的支持度

	public static void main(String args[]) {
		/************* 读取数据集 **************/
		transaction = readData("test");
		// 控制台输出记录
		System.out.println("===========读取事务集成功===========");
		printItemset(transaction);

		Apriori();// 调用Apriori算法获得频繁项集
		System.out.println("频繁项集已确定。\n\n\n\n\n开始挖掘关联规则：");

		getAssociationRules();// 挖掘关联规则
	}

	/**********************************************
	 * ****************读取数据
	 ********************/
	public static List<List<String>> readData(String url) {
		List<List<String>> transaction = new ArrayList<List<String>>();
		String fpath = url + ".txt";
		try {
			String encoding = "UTF-8"; // 字符编码(可解决中文乱码问题 )
			File file = new File(fpath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);

				String lineTXT = null;
				String[] param = bufferedReader.readLine().split(" ");
				kMinSupport = Double.parseDouble(param[0]);
				kMinConfidence = Double.parseDouble(param[1]);
				while ((lineTXT = bufferedReader.readLine()) != "--END--") {// 读一行文件

					String[] lineString = lineTXT.split(" ");

					List<String> lineList = new ArrayList<String>();
					for (int i = 0; i < lineString.length; i++) {
						lineList.add(lineString[i]);
					}
					transaction.add(lineList);
				}

				read.close();
			} else {
				System.out.println("找不到指定的文件！");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容操作出错");
			e.printStackTrace();
		}
		return transaction;
	}

	public static void Apriori() /** 实现apriori算法 **/
	{
		// ************获取候选1项集**************
		System.out.println("第一次扫描后的候选1-项集");
		List<List<String>> candidate_itemset = findFirstCandidate();
		printItemset(candidate_itemset);

		// ************获取频繁1项集***************
		System.out.println("第一次扫描后的频繁1-项集");
		List<List<String>> frequent_itemset = getSupprotedItemset(candidate_itemset);
		insertFrequentItemset(frequent_itemset);// 添加到所有的频繁项集中
		// 控制台输出频繁1项集
		printItemset(frequent_itemset);

		// *****************************迭代过程**********************************
		k = 2;
		while (endTag != true) {

			System.out.println("第" + k + "次扫描后候选集");
			// **********连接操作****获取候选k-项集**************
			List<List<String>> next_candidate_itemset = generateApriori(frequent_itemset);
			// 输出所有的候选项集
			printItemset(next_candidate_itemset);

			/************** 计数操作***由候选k项集选择出频繁k项集 ****************/
			System.out.println("第" + k + "次扫描后频繁集");
			List<List<String>> next_frequent_itemset = getSupprotedItemset(next_candidate_itemset);
			insertFrequentItemset(next_frequent_itemset);// 添加到所有的频繁项集中
			// 输出所有的频繁项集
			printItemset(next_frequent_itemset);

			// *********如果循环结束，输出最大模式**************
			if (endTag == true) {
				System.out.println("\n\n\nApriori算法--->最大频繁项集：");
				printItemset(frequent_itemset);
			}
			// ****************下一次循环初值********************
			frequent_itemset = next_frequent_itemset;
			k++;// 迭代次数加一
		}
	}

	public static void getAssociationRules()// 关联规则挖掘
	{
		for (int i = 0; i < frequent_itemsets.size(); i++) {
			List<String> tem = frequent_itemsets.get(i);
			if (tem.size() > 1) {
				List<String> temclone = new ArrayList<>(tem);
				List<List<String>> all_subset = getSubset(temclone);// 得到频繁项集tem的所有子集
				for (int j = 0; j < all_subset.size(); j++) {
					List<String> s1 = all_subset.get(j);
					List<String> s2 = getS2(tem, s1);
					double conf = isAssociationRules(s1, s2, tem);
					if (conf > 0)
						System.out.println("置信度为：" + conf);
				}
			}

		}
	}

	public static double isAssociationRules(List<String> s1, List<String> s2, List<String> tem)// 判断是否为关联规则
	{
		double confidence = 0;
		int counts1;
		int countTem;
		if (s1.size() != 0 && s1 != null && tem.size() != 0 && tem != null) {
			counts1 = getCount(s1);
			countTem = getCount(tem);
			confidence = countTem * 1.0 / counts1;

			if (confidence >= kMinConfidence) {
				System.out.print(s1.toString() + "------>" + s2.toString() + "   ");
				return confidence;
			} else
				return 0;

		} else
			return 0;
	}

	public static int getCount(List<String> in)// 根据频繁项集得到 其支持度计数
	{
		int rt = 0;
		for (int i = 0; i < map.size(); i++) {
			Map tem = map.get(i);
			if (tem.isListEqual(in)) {
				rt = tem.getcount();
				return rt;
			}
		}
		return rt;

	}

	public static List<String> getS2(List<String> tem, List<String> s1)// 计算tem减去s1后的集合即为s2
	{
		List<String> result = new ArrayList<>();

		for (int i = 0; i < tem.size(); i++)// 去掉s1中的所有元素
		{
			String t = tem.get(i);
			if (!s1.contains(t))
				result.add(t);
		}
		return result;
	}

	public static List<List<String>> getSubset(List<String> set) {
		List<List<String>> result = new ArrayList<>(); // 用来存放子集的集合，如{{},{1},{2},{1,2}}
		int length = set.size();
		int num = length == 0 ? 0 : 1 << (length); // 2的n次方，若集合set为空，num为0；若集合set有4个元素，那么num为16.

		// 从0到2^n-1（[00...00]到[11...11]）
		for (int i = 1; i < num - 1; i++) {
			List<String> subSet = new ArrayList<>();

			int index = i;
			for (int j = 0; j < length; j++) {
				if ((index & 1) == 1) { // 每次判断index最低位是否为1，为1则把集合set的第j个元素放到子集中
					subSet.add(set.get(j));
				}
				index >>= 1; // 右移一位
			}

			result.add(subSet); // 把子集存储起来
		}
		return result;
	}

	public static boolean insertFrequentItemset(List<List<String>> fre) {
		for (int i = 0; i < fre.size(); i++) {
			frequent_itemsets.add(fre.get(i));
		}
		return true;
	}

	public static void printItemset(List<List<String>> candidate_itemset)// 显示出candidateitem中的所有的项集
	{
		for (int i = 0; i < candidate_itemset.size(); i++) {
			List<String> list = new ArrayList<String>(candidate_itemset.get(i));
			for (int j = 0; j < list.size(); j++) {
				System.out.print(list.get(j) + " ");
			}
			System.out.println();
		}
	}

	/**
	 ******************************************************* 由当前频繁项集自连接求下一次候选集
	 */
	private static List<List<String>> generateApriori(List<List<String>> frequent_itemset) {
		List<List<String>> next_candidate_itemset = new ArrayList<List<String>>();

		for (int i = 0; i < frequent_itemset.size(); i++) {
			HashSet<String> hsSet = new HashSet<String>();
			HashSet<String> hsSettemp = new HashSet<String>();
			for (int k = 0; k < frequent_itemset.get(i).size(); k++)// 获得频繁集第i行
				hsSet.add(frequent_itemset.get(i).get(k));
			int hsLength_before = hsSet.size();// 添加前长度
			hsSettemp = (HashSet<String>) hsSet.clone();
			for (int h = i + 1; h < frequent_itemset.size(); h++) {// 频繁集第i行与第j行(j>i)连接 每次添加且添加一个元素组成 新的频繁项集的某一行，
				hsSet = (HashSet<String>) hsSettemp.clone();// ！！！做连接的hasSet保持不变
				for (int j = 0; j < frequent_itemset.get(h).size(); j++)
					hsSet.add(frequent_itemset.get(h).get(j));
				int hsLength_after = hsSet.size();
				if (hsLength_before + 1 == hsLength_after && isnotHave(hsSet, next_candidate_itemset)) {
					// 如果不相等，表示添加了1个新的元素 同时判断其不是候选集中已经存在的一项
					Iterator<String> itr = hsSet.iterator();
					List<String> tempList = new ArrayList<String>();
					while (itr.hasNext()) {
						String Item = (String) itr.next();
						tempList.add(Item);
					}
					next_candidate_itemset.add(tempList);
				}

			}
		}
		return next_candidate_itemset;
	}

	/**
	 * 判断新添加元素形成的候选集是否在新的候选集中
	 */
	private static boolean isnotHave(HashSet<String> hsSet, List<List<String>> next_candidate_itemset) {// 判断hsset是不是candidateitemset中的一项
		List<String> tempList = new ArrayList<String>();
		Iterator<String> itr = hsSet.iterator();
		while (itr.hasNext()) {// 将hsset转换为List<String>
			String Item = (String) itr.next();
			tempList.add(Item);
		}
		for (int i = 0; i < next_candidate_itemset.size(); i++)// 遍历candidateitemset，看其中是否有和templist相同的一项
			if (tempList.equals(next_candidate_itemset.get(i)))
				return false;
		return true;
	}

	/**
	 * 由k项候选集剪枝得到k项频繁集
	 * 
	 * @param candidate_itemset
	 * @return
	 */
	private static List<List<String>> getSupprotedItemset(List<List<String>> candidate_itemset) { // 对所有的商品进行支持度计数
		// TODO Auto-generated method stub
		boolean end = true;
		List<List<String>> supported_itemset = new ArrayList<List<String>>();

		for (int i = 0; i < candidate_itemset.size(); i++) {

			int count = getTransactionCount(candidate_itemset.get(i));// 统计记录数

			if (count >= kMinSupport * (transaction.size() - 1)) {
				supported_itemset.add(candidate_itemset.get(i));
				map.add(new Map(candidate_itemset.get(i), count));// 存储当前频繁项集以及它的支持度计数
				end = false;
			}
		}
		endTag = end;// 存在频繁项集则不会结束
		if (endTag == true)
			System.out.println("无满足支持度的" + k + "项集,结束连接");
		return supported_itemset;
	}

	/**
	 * 统计transaction中出现list集合的个数
	 */
	private static int getTransactionCount(List<String> list) {// 遍历事务集transaction，对单个候选集进行支持度计数

		int count = 0;
		for (int i = 0; i < transaction.size(); i++)// 从transaction的第一个开始遍历
		{
			boolean flag = true;
			for (int j = 0; j < list.size(); j++)// 如果transaction中的第一个数据集包含list中的所有元素
			{
				String t = list.get(j);
				if (!transaction.get(i).contains(t)) {
					flag = false;
					break;
				}
			}
			if (flag)
				count++;// 支持度加一
		}

		return count;// 返回支持度计数

	}

	/**
	 * 获得一项候选集
	 * 
	 * @return
	 */
	private static List<List<String>> findFirstCandidate() {
		// TODO Auto-generated method stub
		List<List<String>> tableList = new ArrayList<List<String>>();
		HashSet<String> hs = new HashSet<String>();// 新建一个hash表，存放所有的不同的一维数据

		for (int i = 1; i < transaction.size(); i++) { // 遍历所有的数据集，找出所有的不同的商品存放到hs中
			for (int j = 1; j < transaction.get(i).size(); j++) {
				hs.add(transaction.get(i).get(j));
			}
		}
		Iterator<String> itr = hs.iterator();
		while (itr.hasNext()) {
			List<String> tempList = new ArrayList<String>();
			String Item = (String) itr.next();
			tempList.add(Item); // 将每一种商品存放到一个List<String>中
			tableList.add(tempList);// 所有的list<String>存放到一个大的list中
		}
		return tableList;// 返回所有的商品
	}
}
