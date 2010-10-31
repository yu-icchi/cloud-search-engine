package location.qbss;

import java.util.ArrayList;
import java.util.List;

public class URLSetTest {

	public static void main(String[] args) {

		List<String> list1 = new ArrayList<String>();
		list1.add("http://localhost:8983/solr/");
		list1.add("http://localhost:7574/solr/");

		List<String> list2 = new ArrayList<String>();
		list2.add("http://localhost:8983/solr/");

		for (int i = 0; i < list1.size(); i++) {
			if (list2.contains(list1.get(i))) {
				System.out.println("AND: " + list1.get(i));
			}
		}

		for (int i = 0; i < list1.size(); i++) {
			if (!list2.contains(list1.get(i))) {
				list2.add(list1.get(i));
			}
		}
		System.out.println(list2);

		for (int i = 0; i < list1.size(); i++) {
			if (!list2.contains(list1.get(i))) {
				System.out.println("NOT: " + list1.get(i));
			}
		}

	}
}
