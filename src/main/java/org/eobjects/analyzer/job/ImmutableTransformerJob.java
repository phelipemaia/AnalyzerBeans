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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.data.MutableInputColumn;
import org.eobjects.analyzer.descriptors.ConfiguredPropertyDescriptor;
import org.eobjects.analyzer.descriptors.TransformerBeanDescriptor;
import org.eobjects.analyzer.util.CollectionUtils;

public final class ImmutableTransformerJob implements TransformerJob {

	private final String _name;
	private final TransformerBeanDescriptor<?> _descriptor;
	private final BeanConfiguration _beanConfiguration;
	private final List<MutableInputColumn<?>> _output;
	private final Outcome _requirement;

	public ImmutableTransformerJob(String name, TransformerBeanDescriptor<?> descriptor,
			BeanConfiguration beanConfiguration, Collection<MutableInputColumn<?>> output, Outcome requirement) {
		_name = name;
		_descriptor = descriptor;
		_beanConfiguration = beanConfiguration;
		_output = Collections.unmodifiableList(new ArrayList<MutableInputColumn<?>>(output));
		_requirement = LazyOutcomeUtils.load(requirement);
	}
	
	@Override
	public String getName() {
		return _name;
	}

	@Override
	public TransformerBeanDescriptor<?> getDescriptor() {
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
			InputColumn<?>[] inputs = CollectionUtils.arrayOf(InputColumn.class, property);
			for (InputColumn<?> inputColumn : inputs) {
				result.add(inputColumn);
			}
		}
		return result.toArray(new InputColumn<?>[result.size()]);
	}

	@Override
	public MutableInputColumn<?>[] getOutput() {
		return _output.toArray(new MutableInputColumn<?>[_output.size()]);
	}

	@Override
	public Outcome getRequirement() {
		return _requirement;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_beanConfiguration == null) ? 0 : _beanConfiguration.hashCode());
		result = prime * result + ((_descriptor == null) ? 0 : _descriptor.hashCode());
		result = prime * result + ((_output == null) ? 0 : _output.hashCode());
		result = prime * result + ((_requirement == null) ? 0 : _requirement.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutableTransformerJob other = (ImmutableTransformerJob) obj;
		if (_beanConfiguration == null) {
			if (other._beanConfiguration != null)
				return false;
		} else if (!_beanConfiguration.equals(other._beanConfiguration))
			return false;
		if (_descriptor == null) {
			if (other._descriptor != null)
				return false;
		} else if (!_descriptor.equals(other._descriptor))
			return false;
		if (_output == null) {
			if (other._output != null)
				return false;
		} else if (!_output.equals(other._output))
			return false;
		if (_requirement == null) {
			if (other._requirement != null)
				return false;
		} else if (!_requirement.equals(other._requirement))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ImmutableTransformerJob[transformer=" + _descriptor.getDisplayName() + "]";
	}
}
