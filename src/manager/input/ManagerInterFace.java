//---------------------------------------------------------
//ManagerInterFaceクラス
//
//managerサーバの制御をするインターフェース
//
//【コマンド】
//	・status
//		LSEクラスタのステータス情報表示
//	・attach
//		新しくLSEノード追加する・死んだLSEノードを復旧させる
//	・detach
//		死んでるLSEノードを削除する
//	・exit or quit
//		プログラムを終了させる
//---------------------------------------------------------
package manager.input;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ManagerInterFace {

	//-----------------------------------------------------
	//プロパティ
	//-----------------------------------------------------

	private List<String> consistentHashing = new ArrayList<String>();

	//-----------------------------------------------------
	//ゲッター・セッター
	//-----------------------------------------------------

	public void setConsistentHashing(List<String> consistentHashing) {
		this.consistentHashing = consistentHashing;
	}

	public List<String> getConsistentHashing() {
		return consistentHashing;
	}

	//-----------------------------------------------------
	//メイン
	//-----------------------------------------------------

	public static void main(String[] args) {
		try {
			//キーボードから入力する
			BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
			String line;
			System.out.print("manager : ");
			while ((line = stdReader.readLine()) != null) {
				//コマンド制御する
				if (line.equals("exit") || line.equals("quit")) {
					//終了させる
					System.exit(0);
				} else if (line.equals("attach")) {
					//ノードの新規追加・復旧
					System.out.println("追加させる");
				} else if (line.equals("status")) {
					//クラスタの情報表示
				} else if (line.equals("detach")) {
					//ノードの削除
				}
				System.out.println(line);
				String[] arr = line.split(" ");
				System.out.println(arr[0]);
				System.out.print("manager : ");
			}
			stdReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			//プログラムを強制終了する
			System.exit(-1);
		}
	}

}
