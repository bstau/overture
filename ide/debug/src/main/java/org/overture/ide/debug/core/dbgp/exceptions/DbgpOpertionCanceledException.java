/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.exceptions;

public class DbgpOpertionCanceledException extends DbgpException {

	private static final long serialVersionUID = 1L;

	public DbgpOpertionCanceledException() {
		super();
	}

	public DbgpOpertionCanceledException(String message, Throwable cause) {
		super(message, cause);
	}

	public DbgpOpertionCanceledException(String message) {
		super(message);
	}

	public DbgpOpertionCanceledException(Throwable cause) {
		super(cause);
	}
}
