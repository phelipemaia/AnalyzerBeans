/**
 * AnalyzerBeans
 * Copyright (C) 2014 Neopost - Customer Information Management
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
package org.eobjects.analyzer.beans.valuedist;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.inject.Inject;

import org.eobjects.analyzer.beans.api.Analyzer;
import org.eobjects.analyzer.beans.api.AnalyzerBean;
import org.eobjects.analyzer.beans.api.ColumnProperty;
import org.eobjects.analyzer.beans.api.Concurrent;
import org.eobjects.analyzer.beans.api.Configured;
import org.eobjects.analyzer.beans.api.Description;
import org.eobjects.analyzer.beans.api.Provided;
import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.data.InputRow;
import org.eobjects.analyzer.storage.CollectionFactory;
import org.eobjects.analyzer.storage.CollectionFactoryImpl;
import org.eobjects.analyzer.storage.InMemoryRowAnnotationFactory;
import org.eobjects.analyzer.storage.InMemoryStorageProvider;
import org.eobjects.analyzer.storage.RowAnnotationFactory;
import org.eobjects.analyzer.util.NullTolerableComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnalyzerBean("Value distribution")
@Description("Gets the distributions of values that occur in a dataset.\nOften used as an initial way to see if a lot of repeated values are to be expected, if nulls occur and if a few un-repeated values add exceptions to the typical usage-pattern.")
@Concurrent(true)
public class ValueDistributionAnalyzer implements Analyzer<ValueDistributionAnalyzerResult> {

    private static final Logger logger = LoggerFactory.getLogger(ValueDistributionAnalyzer.class);

    @Inject
    @Configured(value = "Column", order = 1)
    @ColumnProperty(escalateToMultipleJobs = true)
    InputColumn<?> _column;

    @Inject
    @Configured(value = "Group column", required = false, order = 2)
    InputColumn<String> _groupColumn;

    @Inject
    @Configured(value = "Record unique values", required = false, order = 3)
    boolean _recordUniqueValues = true;

    @Inject
    @Configured(value = "Record drill-down information", required = false, order = 4)
    @Description("Record extra information to allow drilling to the records that represent a particular value in the distribution")
    boolean _recordDrillDownInformation = true;

    @Inject
    @Configured(value = "Top n most frequent values", required = false, order = 5)
    @Deprecated
    Integer _topFrequentValues;

    @Inject
    @Configured(value = "Bottom n most frequent values", required = false, order = 6)
    @Deprecated
    Integer _bottomFrequentValues;

    @Inject
    @Provided
    CollectionFactory _collectionFactory;

    @Inject
    @Provided
    RowAnnotationFactory _annotationFactory;

    private final Map<String, ValueDistributionGroup> _valueDistributionGroups;

    /**
     * Constructor used for testing and ad-hoc purposes
     * 
     * @param column
     * @param recordUniqueValues
     * @param topFrequentValues
     * @param bottomFrequentValues
     */
    public ValueDistributionAnalyzer(InputColumn<?> column, boolean recordUniqueValues, Integer topFrequentValues,
            Integer bottomFrequentValues) {
        this(column, null, recordUniqueValues, topFrequentValues, bottomFrequentValues);
    }

    /**
     * Constructor used for testing and ad-hoc purposes
     * 
     * @param column
     * @param groupColumn
     * @param recordUniqueValues
     * @param topFrequentValues
     * @param bottomFrequentValues
     */
    public ValueDistributionAnalyzer(InputColumn<?> column, InputColumn<String> groupColumn,
            boolean recordUniqueValues, Integer topFrequentValues, Integer bottomFrequentValues) {
        this();
        _column = column;
        _groupColumn = groupColumn;
        _recordUniqueValues = recordUniqueValues;
        _topFrequentValues = topFrequentValues;
        _bottomFrequentValues = bottomFrequentValues;
        _collectionFactory = new CollectionFactoryImpl(new InMemoryStorageProvider());
        _annotationFactory = new InMemoryRowAnnotationFactory();
    }

    /**
     * Main constructor
     */
    public ValueDistributionAnalyzer() {
        _valueDistributionGroups = new TreeMap<String, ValueDistributionGroup>(
                NullTolerableComparator.get(String.class));
    }

    @Override
    public void run(InputRow row, int distinctCount) {
        final Object value = row.getValue(_column);
        if (_groupColumn == null) {
            runInternal(row, value, distinctCount);
        } else {
            final String group = row.getValue(_groupColumn);
            runInternal(row, value, group, distinctCount);
        }
    }

    public void runInternal(InputRow row, Object value, int distinctCount) {
        runInternal(row, value, _column.getName(), distinctCount);
    }

    public void runInternal(InputRow row, Object value, String group, int distinctCount) {
        final ValueDistributionGroup valueDistributionGroup = getValueDistributionGroup(group);
        final String stringValue;
        if (value == null) {
            logger.debug("value is null");
            stringValue = null;
        } else {
            stringValue = value.toString();
        }
        valueDistributionGroup.run(row, stringValue, distinctCount);
    }

    private ValueDistributionGroup getValueDistributionGroup(String group) {
        ValueDistributionGroup valueDistributionGroup = _valueDistributionGroups.get(group);
        if (valueDistributionGroup == null) {
            synchronized (this) {
                valueDistributionGroup = _valueDistributionGroups.get(group);
                if (valueDistributionGroup == null) {
                    final InputColumn<?>[] inputColumns;
                    if (_groupColumn == null) {
                        inputColumns = new InputColumn[] { _column };
                    } else {
                        inputColumns = new InputColumn[] { _column, _groupColumn };
                    }
                    valueDistributionGroup = new ValueDistributionGroup(group, _collectionFactory, _annotationFactory,
                            _recordDrillDownInformation, inputColumns);
                    _valueDistributionGroups.put(group, valueDistributionGroup);
                }
            }
        }
        return valueDistributionGroup;
    }

    @Override
    public ValueDistributionAnalyzerResult getResult() {
        if (_groupColumn == null) {
            logger.info("getResult() invoked, processing single group");
            final ValueDistributionGroup valueDistributionGroup = getValueDistributionGroup(_column.getName());
            final SingleValueDistributionResult ungroupedResult = valueDistributionGroup.createResult(
                    _topFrequentValues, _bottomFrequentValues, _recordUniqueValues);
            return ungroupedResult;
        } else {
            logger.info("getResult() invoked, processing {} groups", _valueDistributionGroups.size());

            final SortedSet<SingleValueDistributionResult> groupedResults = new TreeSet<SingleValueDistributionResult>();
            for (String group : _valueDistributionGroups.keySet()) {
                final ValueDistributionGroup valueDistributibutionGroup = getValueDistributionGroup(group);
                final SingleValueDistributionResult result = valueDistributibutionGroup.createResult(
                        _topFrequentValues, _bottomFrequentValues, _recordUniqueValues);
                groupedResults.add(result);
            }
            return new GroupedValueDistributionResult(_column, _groupColumn, groupedResults);
        }
    }

    public void setAnnotationFactory(RowAnnotationFactory annotationFactory) {
        _annotationFactory = annotationFactory;
    }

    public void setCollectionFactory(CollectionFactory collectionFactory) {
        _collectionFactory = collectionFactory;
    }

    /**
     * 
     * @param bottomFrequentValues
     * @deprecated use of this property is no longer adviced. It will be phased
     *             out in later versions of AnalyzerBeans
     */
    @Deprecated
    public void setBottomFrequentValues(Integer bottomFrequentValues) {
        _bottomFrequentValues = bottomFrequentValues;
    }

    public void setColumn(InputColumn<?> column) {
        _column = column;
    }

    public void setGroupColumn(InputColumn<String> groupColumn) {
        _groupColumn = groupColumn;
    }

    public void setRecordDrillDownInformation(boolean recordDrillDownInformation) {
        _recordDrillDownInformation = recordDrillDownInformation;
    }

    public void setRecordUniqueValues(boolean recordUniqueValues) {
        _recordUniqueValues = recordUniqueValues;
    }

    /**
     * 
     * @param topFrequentValues
     * 
     * @deprecated use of this property is no longer adviced. It will be phased
     *             out in later versions of AnalyzerBeans
     */
    @Deprecated
    public void setTopFrequentValues(Integer topFrequentValues) {
        _topFrequentValues = topFrequentValues;
    }
}
