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
package org.eobjects.analyzer.beans.writers;

import org.eobjects.analyzer.connection.Datastore;
import org.eobjects.analyzer.connection.DatastoreCatalog;
import org.eobjects.analyzer.connection.DatastoreConnection;
import org.eobjects.metamodel.schema.Table;

/**
 * Default implementation of {@link WriteDataResult}.
 * 
 * @author Kasper Sørensen
 */
class WriteDataResultImpl implements WriteDataResult {

	private static final long serialVersionUID = 1L;

	private final int _writtenRowCount;
	private final Datastore _datastore;
	private final String _schemaName;
	private final String _tableName;

	public WriteDataResultImpl(int writtenRowCount, Datastore datastore,
			String schemaName, String tableName) {
		_writtenRowCount = writtenRowCount;
		_datastore = datastore;
		_schemaName = schemaName;
		_tableName = tableName;
	}

	@Override
	public int getWrittenRowCount() {
		return _writtenRowCount;
	}

	@Override
	public Datastore getDatastore(DatastoreCatalog datastoreCatalog) {
		return _datastore;
	}

	@Override
	public Table getPreviewTable(Datastore datastore) {
		DatastoreConnection con = datastore.openConnection();
		try {
			return con.getSchemaNavigator().convertToTable(_schemaName,
					_tableName);
		} finally {
			con.close();
		}
	}

	@Override
	public String toString() {
		return _writtenRowCount + " records written to table '" + _tableName
				+ "' (in datastore '" + _datastore.getName() + "')";
	}
}
