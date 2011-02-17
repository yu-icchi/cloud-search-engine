//---------------------------------------------------------
//Local Search Engine Interface定義
//---------------------------------------------------------
package lse;

public interface LSE {

	String hello(String msg);

	/**
	 * 全てのデーモンスレッドを起動させる
	 */
	void startAll();

	/**
	 * 全てのデーモンスレッドを停止させる
	 */
	void stopAll();

	/**
	 * Location Serverへの更新要求
	 * @param flag
	 */
	void lsCheckFlag(boolean flag);

	/**
	 * 新規ノード追加
	 * @param node
	 */
	void addNode(String node);

	/**
	 * 削除ノード指定
	 * @param node
	 */
	void delNode(String node);

}
