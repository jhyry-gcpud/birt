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

package org.eclipse.birt.report.model.api.scripts;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.model.api.metadata.IClassInfo;
import org.eclipse.birt.report.model.api.metadata.IMemberInfo;
import org.eclipse.birt.report.model.api.metadata.IMethodInfo;
import org.eclipse.birt.report.model.api.util.StringUtil;

/**
 * Represents the script object definition. This definition defines one
 * constructor, several members and methods. It also includes the name, display
 * name ID, and tool tip ID.
 */

public class ClassInfo implements IClassInfo
{

	private final Class<?> clazz;

	/**
	 * The list of method definitions.
	 */

	private Map<String, IMethodInfo> methods;

	/**
	 * The list of member definitions.
	 */

	private Map<String, IMemberInfo> members;

	/**
	 * The constructor definition.
	 */

	private IMethodInfo constructor;

	/**
	 * @param clazz
	 */

	public ClassInfo( Class<?> clazz )
	{
		this.clazz = clazz;
		initialize( );
	}

	private void initialize( )
	{
		methods = new LinkedHashMap<String, IMethodInfo>( );
		members = new LinkedHashMap<String, IMemberInfo>( );

		Method[] classMethods = clazz.getMethods( );
		for ( int i = 0; i < classMethods.length; i++ )
		{
			Method classMethod = classMethods[i];
			String methodName = classMethod.getName( );

			IMethodInfo method = methods.get( methodName );
			if ( method == null )
			{
				method = createMethodInfo( classMethod );
				if ( method != null )
					methods.put( methodName, method );
			}
		}

		Constructor<?>[] classConstructors = clazz.getConstructors( );
		for ( int i = 0; i < classConstructors.length; i++ )
		{
			Constructor<?> classMethod = classConstructors[i];
			if ( constructor == null )
				constructor = createConstructorInfo( classMethod );
		}

		Field[] fields = clazz.getFields( );
		for ( int i = 0; i < fields.length; i++ )
		{
			Field classField = fields[i];
			IMemberInfo memberInfo = createMemberInfo( classField );
			if ( memberInfo != null )
				members.put( classField.getName( ), memberInfo );
		}

	}

	/**
	 * @param classField
	 * @return the member information.
	 */

	protected IMemberInfo createMemberInfo( Field classField )
	{
		return new MemberInfo( classField );
	}

	/**
	 * @param classMethod
	 * @return the constructor information.
	 */

	protected IMethodInfo createConstructorInfo( Constructor<?> classMethod )
	{
		return new ConstructorInfo( classMethod );
	}

	/**
	 * @param classMethod
	 * @return the method information.
	 */

	protected IMethodInfo createMethodInfo( Method classMethod )
	{
		return new MethodInfo( classMethod );
	}

	/**
	 * Returns the method definition list. For methods that have the same name,
	 * only return one method.
	 * 
	 * @return a list of method definitions
	 */

	public List<IMethodInfo> getMethods( )
	{
		if ( methods != null )
			return new ArrayList<IMethodInfo>( methods.values( ) );

		return Collections.emptyList( );
	}

	/**
	 * Get the method definition given the method name.
	 * 
	 * @param name
	 *            the name of the method to get
	 * @return the definition of the method to get
	 */

	public IMethodInfo getMethod( String name )
	{
		return findInfo( methods, name );
	}

	/**
	 * Finds out the member/method information of a <code>ClassInfo</code>.
	 * 
	 * @param objs
	 *            the collection contains member/method information
	 * @param name
	 *            the name of a member/method
	 * 
	 * @return a <code>MemberInfo</code> or a <code>MethodInfo</code>
	 *         corresponding to <code>objs</code>
	 */

	private static IMethodInfo findInfo( Map<String, IMethodInfo> objs,
			String name )
	{
		if ( objs == null || name == null )
			return null;

		return objs.get( name.toLowerCase( ) );
	}

	/**
	 * Returns the list of member definitions.
	 * 
	 * @return the list of member definitions
	 */

	public List<IMemberInfo> getMembers( )
	{
		Field[] fields = clazz.getFields( );
		List<IMemberInfo> retList = new ArrayList<IMemberInfo>( );
		for ( int i = 0; i < fields.length; i++ )
		{
			retList.add( new MemberInfo( fields[i] ) );
		}
		return retList;
	}

	/**
	 * Returns the member definition given method name.
	 * 
	 * @param name
	 *            name of the member to get
	 * @return the member definition to get
	 */

	public IMemberInfo getMember( String name )
	{
		try
		{
			Field field = clazz.getField( name );
			return new MemberInfo( field );
		}
		catch ( NoSuchFieldException e )
		{
			return null;
		}
	}

	/**
	 * Returns the constructor definition.
	 * 
	 * @return the constructor definition
	 */

	public IMethodInfo getConstructor( )
	{
		return constructor;
	}

	/**
	 * Returns whether a class object is native.
	 * 
	 * @return <code>true</code> if an object of this class is native, otherwise
	 *         <code>false</code>
	 */

	public boolean isNative( )
	{
		return false;
	}

	public String getDisplayNameKey( )
	{
		return StringUtil.EMPTY_STRING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.report.model.api.metadata.ILocalizableInfo#getName()
	 */

	public String getName( )
	{
		return clazz.getName( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.report.model.api.metadata.ILocalizableInfo#getToolTipKey
	 * ()
	 */

	public String getToolTipKey( )
	{
		return StringUtil.EMPTY_STRING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.report.model.api.metadata.ILocalizableInfo#getDisplayName
	 * ()
	 */
	public String getDisplayName( )
	{
		return StringUtil.EMPTY_STRING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.report.model.api.metadata.ILocalizableInfo#getToolTip()
	 */
	public String getToolTip( )
	{
		return StringUtil.EMPTY_STRING;
	}
}