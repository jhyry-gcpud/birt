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

package org.eclipse.birt.data.engine.impl.document.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.core.archive.RAInputStream;
import org.eclipse.birt.core.util.IOUtil;
import org.eclipse.birt.data.engine.core.DataException;
import org.eclipse.birt.data.engine.i18n.ResourceConstants;
import org.eclipse.birt.data.engine.impl.document.RowSaveUtil;
import org.eclipse.birt.data.engine.impl.document.stream.VersionManager;

/**
 * Read the raw expression data from report document. This instance only read
 * the row one by one.
 */
class ExprDataReader1 implements IExprDataReader
{
	private int currReadIndex;
	private int currRowIndex;

	private int INT_LENGTH;
	
	private DataInputStream rowExprsDis;
	private RAInputStream rowExprsRAIs;
	private RAInputStream rowLenRAIs;
	private DataInputStream rowLenDis;
	
	private int rowCount;
	
	private int version;
	private Map exprValueMap;
	private List exprKeys;
	private int metaOffset;

	/**
	 * @param rowExprsRAIs
	 * @param rowLenRAIs
	 * @param version
	 */
	ExprDataReader1( RAInputStream rowExprsRAIs, RAInputStream rowLenRAIs,
			int version ) throws DataException
	{
		this.INT_LENGTH = IOUtil.INT_LENGTH;
		
		try
		{
			this.rowCount = IOUtil.readInt( rowExprsRAIs );
			int exprCount = IOUtil.readInt( rowExprsRAIs );
			this.exprKeys = new ArrayList();
			this.rowExprsDis = new DataInputStream( rowExprsRAIs );
			for( int i = 0; i < exprCount; i++ )
			{
				this.exprKeys.add( IOUtil.readString( this.rowExprsDis ) );
			}
			this.metaOffset = INT_LENGTH + IOUtil.readInt( this.rowExprsDis ) + INT_LENGTH;
			
		}
		catch ( IOException e )
		{
			throw new DataException( ResourceConstants.RD_LOAD_ERROR,
					e,
					"Result Data" );
		}
		
		
		this.rowExprsRAIs = rowExprsRAIs;
		this.rowLenRAIs = rowLenRAIs;
		this.rowLenDis = new DataInputStream( rowLenRAIs );
		this.version = version;

		this.currReadIndex = 0;
		this.currRowIndex = -1;
		
		
	}
	
	/*
	 * @see org.eclipse.birt.data.engine.impl.document.IExprResultReader#getRowCount()
	 */
	public int getCount( )
	{
		return this.rowCount;
	}
	
	/*
	 * @see org.eclipse.birt.data.engine.impl.document.IExprResultReader#getRowId()
	 */
	public int getRowId( )
	{
		return this.getRowIndex( );
	}

	/*
	 * @see org.eclipse.birt.data.engine.impl.document.IExprResultReader#getRowIndex()
	 */
	public int getRowIndex( )
	{
		if ( this.currRowIndex >= this.rowCount )
			return this.rowCount;
		
		return this.currRowIndex;
	}
	
	/*
	 * @see org.eclipse.birt.data.engine.impl.document.IExprResultReader#next()
	 */
	public boolean next( ) throws DataException
	{
		if( this.currRowIndex < this.rowCount - 1)
		{
			this.currRowIndex++;
			return true;
		}
		else
			return false;
	}
	
	/*
	 * @see org.eclipse.birt.data.engine.impl.document.IExprResultReader#getRowValue()
	 */
	public Map getRowValue( ) throws DataException
	{
		try
		{
			if ( this.rowCount == 0 )
			{
				if ( this.exprValueMap == null )
					this.exprValueMap = this.getValueMap( );
			}
			else if ( currReadIndex < currRowIndex + 1 )
			{
				this.skipTo( currRowIndex );
				this.exprValueMap = this.getValueMap( );
			}
			currReadIndex = currRowIndex + 1;
		}
		catch ( IOException e )
		{
			throw new DataException( ResourceConstants.RD_LOAD_ERROR,
					e,
					"Result Data" );
		}

		return exprValueMap;
	}

	/**
	 * @param absoluteRowIndex
	 * @throws IOException
	 * @throws DataException
	 */
	private void skipTo( int absoluteRowIndex ) throws IOException, DataException
	{
		if ( currReadIndex == absoluteRowIndex )
			return;

		if ( version == VersionManager.VERSION_2_0 )
		{
			int exprCount;
			int gapRows = absoluteRowIndex - currReadIndex;
			for ( int j = 0; j < gapRows; j++ )
			{
				exprCount = IOUtil.readInt( rowExprsDis );
				for ( int i = 0; i < exprCount; i++ )
				{
				//	IOUtil.readString( rowExprsDis );
					IOUtil.readObject( rowExprsDis );
				}
			}
		}
		else if ( version <= VersionManager.VERSION_2_2_1_1 )
		{
			rowLenRAIs.seek( absoluteRowIndex * INT_LENGTH );
			int rowOffsetAbsolute = IOUtil.readInt( rowLenRAIs );
			// metaOffset is the first bytes of row length + expr name string length.
			rowExprsRAIs.seek( rowOffsetAbsolute + this.metaOffset ); 
			rowExprsDis = new DataInputStream( rowExprsRAIs );
		}
		else
		{
			rowLenRAIs.seek( absoluteRowIndex * 8 /*long length*/ );
			long rowOffsetAbsolute = IOUtil.readLong( this.rowLenDis );
			// metaOffset is the first bytes of row length + expr name string length.
			rowExprsRAIs.seek( rowOffsetAbsolute + this.metaOffset ); 
			rowExprsDis = new DataInputStream( rowExprsRAIs );
		}
	}
	
	/**
	 * @throws IOException
	 */
	private Map getValueMap( ) throws IOException
	{
		Map valueMap = new HashMap( );

		int exprCount = IOUtil.readInt( rowExprsDis );
		for ( int i = 0; i < exprCount; i++ )
		{
			String exprID = this.exprKeys.get( i ).toString( );
			Object exprValue = IOUtil.readObject( rowExprsDis );
			if( RowSaveUtil.EXCEPTION_INDICATOR.equals( exprValue ))
				continue;
			valueMap.put( exprID, exprValue );
		}

		return valueMap;
	}

	/*
	 * @see org.eclipse.birt.data.engine.impl.document.IExprResultReader#close()
	 */
	public void close( )
	{
		try
		{
			if ( rowExprsDis != null )
			{
				rowExprsDis.close( );
				rowExprsDis = null;
			}
			
			if ( this.rowLenDis != null )
			{
				this.rowLenDis.close( );
				this.rowLenDis = null;
			}
		}
		catch ( IOException e )
		{
			// ignore read exception
		}
	}
	
}
