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
package org.eobjects.analyzer.job;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.descriptors.AnalyzerBeanDescriptor;
import org.eobjects.analyzer.descriptors.ConfiguredPropertyDescriptor;
import org.eobjects.analyzer.util.CollectionUtils2;

import org.eobjects.metamodel.util.BaseObject;

public final class ImmutableAnalyzerJob extends BaseObject implements AnalyzerJob {

	private final String _name;
	private final AnalyzerBeanDescriptor<?> _descriptor;
	private final BeanConfiguration _beanConfiguration;
	private final Outcome _requirement;

	public ImmutableAnalyzerJob(String name, AnalyzerBeanDescriptor<?> descriptor, BeanConfiguration beanConfiguration,
			Outcome requirement) {
		_name = name;
		_descriptor = descriptor;
		_beanConfiguration = beanConfiguration;
		_requirement = LazyOutcomeUtils.load(requirement);
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public AnalyzerBeanDescriptor<?> getDescriptor() {
		return _descriptor;
	}

	@Override
	public BeanConfiguration getConfiguration() {
		return _beanConfiguration;
	}

	@Override
	public InputColumn<?>[] getInput() {
		List<InputColumn<?>> result = new LinkedList<InputColumn<?>>();
		Set<ConfiguredPropertyDescriptor> propertiesForInput = _descriptor.getConfiguredPropertiesForInput();
		for (ConfiguredPropertyDescriptor propertyDescriptor : propertiesForInput) {
			Object property = _beanConfiguration.getProperty(propertyDescriptor);
			InputColumn<?>[] inputs = CollectionUtils2.arrayOf(InputColumn.class, property);
			if (inputs != null) {
				for (InputColumn<?> inputColumn : inputs) {
					result.add(inputColumn);
				}
			}
		}
		return result.toArray(new InputColumn<?>[result.size()]);
	}

	@Override
	protected void decorateIdentity(List<Object> identifiers) {
		identifiers.add(_name);
		identifiers.add(_beanConfiguration);
		identifiers.add(_descriptor);
		identifiers.add(_requirement);
	}

	@Override
	public String toString() {
		return "ImmutableAnalyzerJob[name=" + _name + ",analyzer=" + _descriptor.getDisplayName() + "]";
	}

	@Override
	public Outcome[] getRequirements() {
		if (_requirement == null) {
			return new Outcome[0];
		}
		return new Outcome[] { _requirement };
	}
}