package org.eobjects.analyzer.beans.valuedist;

import junit.framework.TestCase;

public class ValueCountListImplTest extends TestCase {

	public void testTopList() throws Exception {
		ValueCountListImpl list = ValueCountListImpl.createTopList(5);

		list.register(new ValueCount("1", 1));
		list.register(new ValueCount("2", 2));

		assertEquals("[[2->2], [1->1]]", list.getValueCounts().toString());

		assertEquals(2, list.getActualSize());

		list.register(new ValueCount("3", 3));
		list.register(new ValueCount("4", 4));
		list.register(new ValueCount("5", 5));

		assertEquals(5, list.getActualSize());

		list.register(new ValueCount("6", 6));

		assertEquals(5, list.getActualSize());
		assertEquals("[[6->6], [5->5], [4->4], [3->3], [2->2]]", list
				.getValueCounts().toString());

		list.register(new ValueCount("10", 10));
		list.register(new ValueCount("8", 8));

		assertEquals("[[10->10], [8->8], [6->6], [5->5], [4->4]]", list
				.getValueCounts().toString());
	}

	public void testBottomList() throws Exception {
		ValueCountListImpl list = ValueCountListImpl.createBottomList(5);

		list.register(new ValueCount("40", 40));
		list.register(new ValueCount("30", 30));
		list.register(new ValueCount("50", 50));

		assertEquals(3, list.getActualSize());
		assertEquals("[[30->30], [40->40], [50->50]]", list.getValueCounts()
				.toString());

		list.register(new ValueCount("45", 45));

		list.register(new ValueCount("1", 1));
		list.register(new ValueCount("15", 15));

		assertEquals(5, list.getActualSize());
		assertEquals("[[1->1], [15->15], [30->30], [40->40], [45->45]]", list
				.getValueCounts().toString());
	}
}
