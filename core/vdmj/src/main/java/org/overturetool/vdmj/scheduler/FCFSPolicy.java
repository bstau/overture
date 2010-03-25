/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.scheduler;

import java.util.LinkedList;
import java.util.List;

import org.overturetool.vdmj.config.Properties;

public class FCFSPolicy extends SchedulingPolicy
{
    private static final long serialVersionUID = 1L;
	protected final List<SchedulableThread> threads;
	protected SchedulableThread bestThread = null;
	protected long minimumDuration = -1;

	public FCFSPolicy()
	{
		threads = new LinkedList<SchedulableThread>();
	}

	@Override
	public void reset()
	{
		threads.clear();
		bestThread = null;
		minimumDuration = -1;
	}

	@Override
	public void register(SchedulableThread thread, long priority)
	{
		synchronized (threads)
		{
			threads.add(thread);
		}
	}

	@Override
	public void unregister(SchedulableThread thread)
	{
		synchronized (threads)
		{
			threads.remove(thread);
		}
	}

	@Override
	public boolean reschedule()
	{
		bestThread = null;
		minimumDuration = Long.MAX_VALUE;		// No one is stepping

		synchronized (threads)
		{
    		out: for (SchedulableThread th: threads)
    		{
    			switch (th.getRunState())
    			{
    				case RUNNABLE:
    					minimumDuration = -1;	// Can't step yet
        				bestThread = th;
        				threads.remove(th);
        				threads.add(th);
        				break out;

    				case TIMESTEP:
    					if (th.getTimestep() < minimumDuration)
    					{
    						minimumDuration = th.getTimestep();
    					}
    					break;

    				case WAITING:
    				case LOCKING:
    				default:
    					break;
    			}
    		}
		}

		return (bestThread != null);
	}

	@Override
	public SchedulableThread getThread()
	{
		synchronized (threads)		// As it was set under threads
		{
			return bestThread;
		}
	}

	@Override
	public long getTimeslice()
	{
		if (bestThread.isVirtual())
		{
			return Properties.scheduler_virtual_timeslice;
		}
		else
		{
			return Properties.scheduler_fcfs_timeslice;
		}
	}

	@Override
	public long getTimestep()
	{
		synchronized (threads)		// As it was set under threads
		{
			return minimumDuration;
		}
	}

	@Override
	public void advance()
	{
		synchronized (threads)
		{
    		for (SchedulableThread th: threads)
    		{
    			if (th.getRunState() == RunState.TIMESTEP)
    			{
    				th.setState(RunState.RUNNABLE);
    			}
    		}
		}
	}

	@Override
	public boolean hasActive()
	{
		synchronized (threads)
		{
    		for (SchedulableThread th: threads)
    		{
    			if (th.isActive())
    			{
    				return true;
    			}
    		}
		}

		return false;
	}

	@Override
	public boolean hasPriorities()
	{
		return false;
	}

	@Override
	public String getStatus()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (SchedulableThread th: threads)
		{
			sb.append(sep);
			sb.append(th);
			sep = "\n";
		}

		return sb.toString();
	}
}
