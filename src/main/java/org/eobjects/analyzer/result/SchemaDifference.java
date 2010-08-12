package org.eobjects.analyzer.result;

import dk.eobjects.metamodel.schema.Schema;

public class SchemaDifference<E> implements StructuralDifference<Schema, E> {

	private static final long serialVersionUID = 1L;

	private Schema schema1;
	private Schema schema2;
	private String valueName;
	private E value1;
	private E value2;

	public SchemaDifference(Schema schema1, Schema schema2, String valueName,
			E value1, E value2) {
		this.schema1 = schema1;
		this.schema2 = schema2;
		this.valueName = valueName;
		this.value1 = value1;
		this.value2 = value2;
	}

	@Override
	public Schema getStructure1() {
		return schema1;
	}

	@Override
	public Schema getStructure2() {
		return schema2;
	}

	@Override
	public String getValueName() {
		return valueName;
	}

	@Override
	public E getValue1() {
		return value1;
	}

	@Override
	public E getValue2() {
		return value2;
	}

	@Override
	public String toString() {
		return "Schemas '" + getStructure1().getName() + "' and '"
				+ getStructure2().getName() + "' differ on '" + getValueName()
				+ "': [" + getValue1() + "] vs. [" + getValue2() + "]";
	}
}
