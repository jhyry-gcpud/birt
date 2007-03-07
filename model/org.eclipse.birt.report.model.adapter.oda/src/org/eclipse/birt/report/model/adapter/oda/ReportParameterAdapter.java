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

package org.eclipse.birt.report.model.adapter.oda;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.model.api.OdaDataSetParameterHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;

/**
 * Converts values between a report parameter and ODA Design Session Request.
 * 
 */

public class ReportParameterAdapter implements IReportParameterAdapter
{

	private IReportParameterAdapter adapter;

	/**
	 * The logger for errors.
	 */

	protected static Logger errorLogger = Logger
			.getLogger( ReportParameterAdapter.class.getName( ) );

	/**
	 * Default constructor.
	 */

	public ReportParameterAdapter( )
	{
		try
		{
			Platform.startup( null );
		}
		catch ( BirtException e )
		{
			errorLogger.log( Level.INFO,
					"Error occurs while start the platform", e ); //$NON-NLS-1$
		}

		Object factory = Platform
				.createFactoryObject( IAdapterFactory.EXTENSION_MODEL_ADAPTER_ODA_FACTORY );
		if ( factory instanceof IAdapterFactory )
		{
			adapter = ( (IAdapterFactory) factory ).createReportParameterAdapter( );
		}
		if ( adapter == null )
		{
			errorLogger.log( Level.INFO,
					"Can not start the model adapter oda factory." ); //$NON-NLS-1$
		}
	}

	public void updateLinkedReportParameter( ScalarParameterHandle reportParam,
			OdaDataSetParameterHandle dataSetParam, DataSetDesign dataSetDesign )
			throws SemanticException
	{
		adapter.updateLinkedReportParameter( reportParam, dataSetParam,
				dataSetDesign );
	}

	public void updateLinkedReportParameter( ScalarParameterHandle reportParam,
			OdaDataSetParameterHandle dataSetParam ) throws SemanticException
	{
		adapter.updateLinkedReportParameter( reportParam, dataSetParam );
	}
}
