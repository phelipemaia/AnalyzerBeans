/**
 * eobjects.org AnalyzerBeans
 * Copyright (C) 2010 eobjects.org
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.eobjects.analyzer.beans.transform;

import junit.framework.TestCase;

import org.eobjects.analyzer.beans.api.Transformer;
import org.eobjects.analyzer.data.MockInputColumn;
import org.eobjects.analyzer.data.MockInputRow;
import org.eobjects.analyzer.reference.SynonymCatalog;
import org.eobjects.analyzer.reference.TextFileSynonymCatalog;

public class SynonymReplacementTransformerTest extends TestCase {

	public void testTransform() throws Exception {
		SynonymCatalog sc = new TextFileSynonymCatalog("my synonyms", "src/test/resources/synonym-countries.txt", true,
				"UTF8");
		MockInputColumn<String> col = new MockInputColumn<String>("my col", String.class);

		// with retain original value
		Transformer<String> transformer = new SynonymReplacementTransformer(col, sc, true);
		assertEquals(1, transformer.getOutputColumns().getColumnCount());
		assertEquals("my col (synonyms replaced)", transformer.getOutputColumns().getColumnName(0));

		assertEquals("hello", transformer.transform(new MockInputRow().put(col, "hello"))[0]);
		assertEquals("ALB", transformer.transform(new MockInputRow().put(col, "Albania"))[0]);

		// without retain original value
		transformer = new SynonymReplacementTransformer(col, sc, false);
		assertEquals(1, transformer.getOutputColumns().getColumnCount());
		assertEquals("my col (synonyms replaced)", transformer.getOutputColumns().getColumnName(0));

		assertNull(transformer.transform(new MockInputRow().put(col, "hello"))[0]);
		assertEquals("ALB", transformer.transform(new MockInputRow().put(col, "Albania"))[0]);
	}
}