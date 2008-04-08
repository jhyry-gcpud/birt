/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.chart.ui.swt.type;

import java.util.Collection;
import java.util.Vector;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.DialChart;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.Orientation;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.BaseSampleData;
import org.eclipse.birt.chart.model.data.DataFactory;
import org.eclipse.birt.chart.model.data.OrthogonalSampleData;
import org.eclipse.birt.chart.model.data.SampleData;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.impl.DialChartImpl;
import org.eclipse.birt.chart.model.type.DialSeries;
import org.eclipse.birt.chart.model.type.impl.DialSeriesImpl;
import org.eclipse.birt.chart.ui.extension.i18n.Messages;
import org.eclipse.birt.chart.ui.swt.ChartPreviewPainter;
import org.eclipse.birt.chart.ui.swt.DefaultChartSubTypeImpl;
import org.eclipse.birt.chart.ui.swt.DefaultChartTypeImpl;
import org.eclipse.birt.chart.ui.swt.HelpContentImpl;
import org.eclipse.birt.chart.ui.swt.interfaces.IHelpContent;
import org.eclipse.birt.chart.ui.swt.interfaces.ISelectDataComponent;
import org.eclipse.birt.chart.ui.swt.interfaces.ISelectDataCustomizeUI;
import org.eclipse.birt.chart.ui.swt.wizard.ChartWizardContext;
import org.eclipse.birt.chart.ui.swt.wizard.data.DefaultBaseSeriesComponent;
import org.eclipse.birt.chart.ui.util.ChartCacheManager;
import org.eclipse.birt.chart.ui.util.ChartUIConstants;
import org.eclipse.birt.chart.ui.util.ChartUIUtil;
import org.eclipse.birt.chart.ui.util.UIHelper;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.graphics.Image;

/**
 * MeterChart
 */
public class MeterChart extends DefaultChartTypeImpl
{

	/**
	 * Comment for <code>TYPE_LITERAL</code>
	 */
	public static final String TYPE_LITERAL = ChartUIConstants.TYPE_METER;

	private static final String STANDARD_SUBTYPE_LITERAL = "Standard Meter Chart"; //$NON-NLS-1$

	private static final String SUPERIMPOSED_SUBTYPE_LITERAL = "Superimposed Meter Chart"; //$NON-NLS-1$

	public static final String CHART_TITLE = Messages.getString( "MeterChart.Txt.DefaultMeterChartTitle" ); //$NON-NLS-1$

	private static final String sStandardDescription = Messages.getString( "MeterChart.Txt.Description" ); //$NON-NLS-1$

	private static final String sSuperimposedDescription = Messages.getString( "MeterChart.Txt.SuperimposedDescription" ); //$NON-NLS-1$

	private transient Image imgIcon = null;

	private transient Image imgStandard = null;

	private transient Image imgSuperimposed = null;

	private static final String[] saDimensions = new String[]{
		TWO_DIMENSION_TYPE
	};

	public MeterChart( )
	{
		imgIcon = UIHelper.getImage( "icons/obj16/metercharticon.gif" ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.IChartType#getTypeName()
	 */
	public String getName( )
	{
		return TYPE_LITERAL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.IChartType#getTypeName()
	 */
	public Image getImage( )
	{
		return imgIcon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.IChartType#getHelp()
	 */
	public IHelpContent getHelp( )
	{
		return new HelpContentImpl( TYPE_LITERAL,
				Messages.getString( "MeterChart.Txt.HelpText" ) ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getChartSubtypes(java.lang.String)
	 */
	public Collection getChartSubtypes( String sDimension,
			Orientation orientation )
	{
		Vector vSubTypes = new Vector( );
		// Do not respond to requests for unknown orientations
		if ( !orientation.equals( Orientation.VERTICAL_LITERAL ) )
		{
			return vSubTypes;
		}
		if ( sDimension.equals( TWO_DIMENSION_TYPE )
				|| sDimension.equals( ChartDimension.TWO_DIMENSIONAL_LITERAL.getName( ) ) )
		{
			imgStandard = UIHelper.getImage( "icons/wizban/meterchartimage.gif" ); //$NON-NLS-1$
			imgSuperimposed = UIHelper.getImage( "icons/wizban/meterchartsuperimposedimage.gif" ); //$NON-NLS-1$

			vSubTypes.add( new DefaultChartSubTypeImpl( STANDARD_SUBTYPE_LITERAL,
					imgStandard,
					sStandardDescription,
					Messages.getString( "MeterChart.SubType.Standard" ) ) ); //$NON-NLS-1$
			vSubTypes.add( new DefaultChartSubTypeImpl( SUPERIMPOSED_SUBTYPE_LITERAL,
					imgSuperimposed,
					sSuperimposedDescription,
					Messages.getString( "MeterChart.SubType.Superimposed" ) ) ); //$NON-NLS-1$
		}
		return vSubTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getModel(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public Chart getModel( String sSubType, Orientation orientation,
			String sDimension, Chart currentChart )
	{
		DialChart newChart = null;
		if ( currentChart != null )
		{
			newChart = (DialChart) getConvertedChart( currentChart,
					sSubType,
					sDimension );
			if ( newChart != null )
			{
				return newChart;
			}
		}
		newChart = (DialChart) DialChartImpl.create( );
		newChart.setType( TYPE_LITERAL );
		newChart.setSubType( sSubType );
		newChart.setDimension( getDimensionFor( sDimension ) );
		newChart.setUnits( "Points" ); //$NON-NLS-1$

		newChart.setDialSuperimposition( sSubType.equals( SUPERIMPOSED_SUBTYPE_LITERAL ) );
		newChart.getLegend( ).setItemType( LegendItemType.SERIES_LITERAL );

		SeriesDefinition sdX = SeriesDefinitionImpl.create( );
		sdX.getSeriesPalette( ).shift( 0 );
		Series categorySeries = SeriesImpl.create( );
		sdX.getSeries( ).add( categorySeries );
		sdX.getQuery( ).setDefinition( "Base Series" ); //$NON-NLS-1$

		newChart.getTitle( ).getLabel( ).getCaption( ).setValue( CHART_TITLE );

		SeriesDefinition sdY = SeriesDefinitionImpl.create( );
		sdY.getSeriesPalette( ).shift( 0 );
		DialSeries valueSeries = (DialSeries) DialSeriesImpl.create( );
		valueSeries.getLabel( ).setVisible( true );
		valueSeries.setSeriesIdentifier( "valueSeriesIdentifier" ); //$NON-NLS-1$
		sdY.getSeries( ).add( valueSeries );

		sdX.getSeriesDefinitions( ).add( sdY );

		newChart.getSeriesDefinitions( ).add( sdX );

		addSampleData( newChart );
		return newChart;
	}

	private void addSampleData( Chart newChart )
	{
		SampleData sd = DataFactory.eINSTANCE.createSampleData( );
		sd.getBaseSampleData( ).clear( );
		sd.getOrthogonalSampleData( ).clear( );

		// Create Base Sample Data
		BaseSampleData sdBase = DataFactory.eINSTANCE.createBaseSampleData( );
		sdBase.setDataSetRepresentation( "A, B, C" ); //$NON-NLS-1$
		sd.getBaseSampleData( ).add( sdBase );

		// Create Orthogonal Sample Data (with simulation count of 2)
		OrthogonalSampleData oSample = DataFactory.eINSTANCE.createOrthogonalSampleData( );
		oSample.setDataSetRepresentation( "5, 4, 12" ); //$NON-NLS-1$
		oSample.setSeriesDefinitionIndex( 0 );
		sd.getOrthogonalSampleData( ).add( oSample );

		newChart.setSampleData( sd );
	}

	private Chart getConvertedChart( Chart currentChart, String sNewSubType,
			String sNewDimension )
	{
		Chart helperModel = (Chart) EcoreUtil.copy( currentChart );
		// Cache series to keep attributes during conversion
		ChartCacheManager.getInstance( )
				.cacheSeries( ChartUIUtil.getAllOrthogonalSeriesDefinitions( helperModel ) );
		if ( currentChart instanceof ChartWithAxes )
		{
			if ( !ChartPreviewPainter.isLivePreviewActive( ) )
			{
				helperModel.setSampleData( getConvertedSampleData( helperModel.getSampleData( ),
						( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
								.get( 0 ) ).getType( ) ) );
			}

			// Create a new instance of the correct type and set initial
			// properties
			currentChart = DialChartImpl.create( );
			currentChart.setType( TYPE_LITERAL );
			currentChart.setSubType( sNewSubType );
			currentChart.setDimension( getDimensionFor( sNewDimension ) );
			( (DialChart) currentChart ).setDialSuperimposition( false );

			// Copy generic chart properties from the old chart
			currentChart.setBlock( helperModel.getBlock( ) );
			currentChart.setDescription( helperModel.getDescription( ) );
			currentChart.setGridColumnCount( helperModel.getGridColumnCount( ) );

			if ( helperModel.getInteractivity( ) != null )
			{
				currentChart.getInteractivity( )
						.setEnable( helperModel.getInteractivity( ).isEnable( ) );
				currentChart.getInteractivity( )
						.setLegendBehavior( helperModel.getInteractivity( )
								.getLegendBehavior( ) );
			}

			currentChart.setSampleData( helperModel.getSampleData( ) );

			currentChart.setScript( helperModel.getScript( ) );
			currentChart.setUnits( helperModel.getUnits( ) );
			if ( helperModel.getGridColumnCount( ) > 0 )
			{
				currentChart.setGridColumnCount( helperModel.getGridColumnCount( ) );
			}
			else
			{
				currentChart.setGridColumnCount( 0 );
			}

			// Copy series definitions from old chart
			( (ChartWithoutAxes) currentChart ).getSeriesDefinitions( )
					.add( ( (Axis) ( (ChartWithAxes) helperModel ).getAxes( )
							.get( 0 ) ).getSeriesDefinitions( ).get( 0 ) );
			Vector vOSD = new Vector( );

			// Only convert series in primary orthogonal axis.
			Axis primaryOrthogonalAxis = ( (ChartWithAxes) helperModel ).getPrimaryOrthogonalAxis( (Axis) ( (ChartWithAxes) helperModel ).getAxes( )
					.get( 0 ) );
			EList osd = primaryOrthogonalAxis.getSeriesDefinitions( );
			for ( int j = 0; j < osd.size( ); j++ )
			{
				SeriesDefinition sd = (SeriesDefinition) osd.get( j );
				Series series = sd.getDesignTimeSeries( );
				sd.getSeries( ).clear( );
				sd.getSeries( ).add( getConvertedSeries( series, j ) );
				vOSD.add( sd );
			}

			( (SeriesDefinition) ( (ChartWithoutAxes) currentChart ).getSeriesDefinitions( )
					.get( 0 ) ).getSeriesDefinitions( ).clear( );
			( (SeriesDefinition) ( (ChartWithoutAxes) currentChart ).getSeriesDefinitions( )
					.get( 0 ) ).getSeriesDefinitions( ).addAll( vOSD );

			currentChart.getLegend( )
					.setItemType( LegendItemType.SERIES_LITERAL );
			currentChart.getTitle( )
					.getLabel( )
					.getCaption( )
					.setValue( CHART_TITLE );
		}
		else if ( currentChart instanceof ChartWithoutAxes )
		{
			if ( currentChart.getType( ).equals( TYPE_LITERAL ) )
			{
				currentChart.setSubType( sNewSubType );
				( (DialChart) currentChart ).setDialSuperimposition( sNewSubType.equals( SUPERIMPOSED_SUBTYPE_LITERAL ) );
				if ( !currentChart.getDimension( )
						.equals( getDimensionFor( sNewDimension ) ) )
				{
					currentChart.setDimension( getDimensionFor( sNewDimension ) );
				}
			}
			else
			{
				// Create a new instance of the correct type and set initial
				// properties
				currentChart = DialChartImpl.create( );
				currentChart.setType( TYPE_LITERAL );
				currentChart.setSubType( sNewSubType );
				currentChart.setDimension( getDimensionFor( sNewDimension ) );

				( (DialChart) currentChart ).setDialSuperimposition( sNewSubType.equals( SUPERIMPOSED_SUBTYPE_LITERAL ) );

				// Copy generic chart properties from the old chart
				currentChart.setBlock( helperModel.getBlock( ) );
				currentChart.setDescription( helperModel.getDescription( ) );
				currentChart.setGridColumnCount( helperModel.getGridColumnCount( ) );
				currentChart.setSampleData( helperModel.getSampleData( ) );
				currentChart.setScript( helperModel.getScript( ) );
				currentChart.setUnits( helperModel.getUnits( ) );

				if ( helperModel.getInteractivity( ) != null )
				{
					currentChart.getInteractivity( )
							.setEnable( helperModel.getInteractivity( )
									.isEnable( ) );
					currentChart.getInteractivity( )
							.setLegendBehavior( helperModel.getInteractivity( )
									.getLegendBehavior( ) );
				}

				// Clear existing series definitions
				( (ChartWithoutAxes) currentChart ).getSeriesDefinitions( )
						.clear( );

				// Copy series definitions
				( (ChartWithoutAxes) currentChart ).getSeriesDefinitions( )
						.add( ( (ChartWithoutAxes) helperModel ).getSeriesDefinitions( )
								.get( 0 ) );

				// Update the series
				EList seriesdefinitions = ( (SeriesDefinition) ( (ChartWithoutAxes) currentChart ).getSeriesDefinitions( )
						.get( 0 ) ).getSeriesDefinitions( );
				for ( int j = 0; j < seriesdefinitions.size( ); j++ )
				{
					Series series = ( (SeriesDefinition) seriesdefinitions.get( j ) ).getDesignTimeSeries( );
					series = getConvertedSeries( series, j );

					// Clear any existing series
					( (SeriesDefinition) seriesdefinitions.get( j ) ).getSeries( )
							.clear( );
					// Add the new series
					( (SeriesDefinition) seriesdefinitions.get( j ) ).getSeries( )
							.add( series );
				}

				currentChart.getLegend( )
						.setItemType( LegendItemType.SERIES_LITERAL );
				currentChart.getTitle( )
						.getLabel( )
						.getCaption( )
						.setValue( CHART_TITLE );
			}
		}
		else
		{
			return null;
		}
		return currentChart;
	}

	private Series getConvertedSeries( Series series, int seriesIndex )
	{
		// Do not convert base series
		if ( series.getClass( ).getName( ).equals( SeriesImpl.class.getName( ) ) )
		{
			return series;
		}

		DialSeries dialseries = (DialSeries) ChartCacheManager.getInstance( )
				.findSeries( DialSeriesImpl.class.getName( ), seriesIndex );
		if ( dialseries == null )
		{
			dialseries = (DialSeries) DialSeriesImpl.create( );
		}

		// Copy generic series properties
		ChartUIUtil.copyGeneralSeriesAttributes( series, dialseries );

		return dialseries;
	}

	private SampleData getConvertedSampleData( SampleData currentSampleData,
			AxisType axisType )
	{
		// Convert base sample data
		EList bsdList = currentSampleData.getBaseSampleData( );
		Vector vNewBaseSampleData = new Vector( );
		for ( int i = 0; i < bsdList.size( ); i++ )
		{
			BaseSampleData bsd = (BaseSampleData) bsdList.get( i );
			bsd.setDataSetRepresentation( ChartUIUtil.getConvertedSampleDataRepresentation( axisType,
					bsd.getDataSetRepresentation( ),
					i ) );
			vNewBaseSampleData.add( bsd );
		}
		currentSampleData.getBaseSampleData( ).clear( );
		currentSampleData.getBaseSampleData( ).addAll( vNewBaseSampleData );

		// Convert orthogonal sample data
		EList osdList = currentSampleData.getOrthogonalSampleData( );
		Vector vNewOrthogonalSampleData = new Vector( );
		for ( int i = 0; i < osdList.size( ); i++ )
		{
			OrthogonalSampleData osd = (OrthogonalSampleData) osdList.get( i );
			osd.setDataSetRepresentation( ChartUIUtil.getConvertedSampleDataRepresentation( AxisType.LINEAR_LITERAL,
					osd.getDataSetRepresentation( ),
					i ) );
			vNewOrthogonalSampleData.add( osd );
		}
		currentSampleData.getOrthogonalSampleData( ).clear( );
		currentSampleData.getOrthogonalSampleData( )
				.addAll( vNewOrthogonalSampleData );
		return currentSampleData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getSupportedDimensions()
	 */
	public String[] getSupportedDimensions( )
	{
		return saDimensions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getDefaultDimension()
	 */
	public String getDefaultDimension( )
	{
		return saDimensions[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#supportsTransposition()
	 */
	public boolean supportsTransposition( )
	{
		return false;
	}

	private ChartDimension getDimensionFor( String sDimension )
	{
		return ChartDimension.TWO_DIMENSIONAL_LITERAL;
	}

	public ISelectDataComponent getBaseUI( Chart chart,
			ISelectDataCustomizeUI selectDataUI, ChartWizardContext context,
			String sTitle )
	{
		DefaultBaseSeriesComponent component = new DefaultBaseSeriesComponent( (SeriesDefinition) ChartUIUtil.getBaseSeriesDefinitions( chart )
				.get( 0 ),
				context,
				sTitle );
		component.setLabelText( Messages.getString( "PieBaseSeriesComponent.Label.CategoryDefinition" ) ); //$NON-NLS-1$
		component.setTooltipWhenBlank( Messages.getString( "MeterChart.Tooltip.InputExpression" ) ); //$NON-NLS-1$
		return component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.DefaultChartTypeImpl#getDisplayName()
	 */
	public String getDisplayName( )
	{
		return Messages.getString( "MeterChart.Txt.DisplayName" ); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getSeries()
	 */
	public Series getSeries( )
	{
		return DialSeriesImpl.create( );
	}
}
