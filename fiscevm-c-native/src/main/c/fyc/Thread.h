/**
 *  Copyright 2010-2013 Yuxuan Huang. All rights reserved.
 *
 * This file is part of fiscevm
 *
 *fiscevmis free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 *fiscevmis distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with fiscevm  If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef FY_THREAD_H_
#define FY_THREAD_H_

#include <math.h>
#include <float.h>
#include "../fisceprt.h"
#include "../fiscestu.h"
#include "../fiscedev.h"
#include "../fy_util/Debug.h"
#include "ThreadManager.h"
#include "Preverifier.h"
#ifdef	__cplusplus
extern "C" {
#endif
void fy_threadSetCurrentThrowable(fy_context *context, fy_thread *thread,
		fy_int handle, fy_exception *exception);

fy_int fy_threadMonitorEnter(fy_context *context, fy_thread *thread,
		fy_int handle, fy_int ops);

fy_int fy_threadMonitorExit(fy_context *context, fy_thread *thread,
		fy_int handle, fy_int ops, fy_exception *exception);

void fy_threadDestroy(fy_context *context, fy_thread *thread);

fy_method *fy_threadGetCurrentMethod(fy_context *context, fy_thread *thread);

void fy_threadInitWithRun(fy_context *context, fy_thread *thread, int handle,
		fy_exception *exception);

void fy_threadInitWithMethod(fy_context *context, fy_thread *thread,
		int threadHandle, fy_method *method, fy_exception *exception);

fy_frame *fy_threadCurrentFrame(fy_context *context, fy_thread *thread);

fy_int fy_threadPushMethod(fy_context *context, fy_thread *thread,
		fy_method *invoke, fy_stack_item *spp, fy_int ops, fy_exception *exception);

fy_int fy_threadInvoke(fy_context *context, fy_thread *thread,
		fy_method *method, fy_stack_item *spp, fy_int ops, fy_exception *exception);

fy_int fy_threadClinit(fy_context *context, fy_thread *thread, fy_class *clazz,
		fy_stack_item *spp, fy_int ops, fy_exception *exception);

void fy_threadRun(fy_context *context, fy_thread *thread, fy_message *message,
		fy_int ops, fy_exception *exception);

void fy_threadFillException(fy_context *context, fy_thread *thread,
		fy_uint handle, fy_exception *exception);

fy_uint fy_threadPrepareThrowable(fy_context *context, fy_thread *thread,
		FY_ATTR_RESTRICT fy_exception *toPrepare,
		FY_ATTR_RESTRICT fy_exception *exception);


void fy_threadReturnInt(fy_stack_item *spp, fy_int value);

void fy_threadReturnLong(fy_stack_item *spp, fy_long value);

void fy_threadScanRef(fy_context *context, fy_thread *thread,
		fy_arrayList *from, fy_exception *exception);

#define fy_threadReturnFloat(SPP,V) fy_threadReturnInt(SPP,fy_floatToInt(V))

#define fy_threadReturnDouble(SPP,V) fy_threadReturnLong(SPP,fy_doubleToLong(V))

fy_frame *fy_threadPushFrame(fy_context *context, fy_thread *thread,
		fy_method *invoke, fy_stack_item *spp, fy_exception *exception);
fy_frame *fy_threadPopFrame(fy_context *context, fy_thread *thread);

fy_int fy_threadInvokeSpecial(fy_context *context, fy_thread *thread,
		fy_frame *frame, fy_method *method, fy_stack_item *spp, fy_int ops,
		fy_exception *exception);
fy_int fy_threadInvokeVirtual(fy_context *context, fy_thread *thread,
		fy_frame *frame, fy_method *method, fy_stack_item *spp, fy_int ops,
		fy_exception *exception);
fy_int fy_threadInvokeStatic(fy_context *context, fy_thread *thread,
		fy_frame *frame, fy_method *method, fy_stack_item *spp, fy_int ops,
		fy_exception *exception);

#ifdef	__cplusplus
}
#endif
#endif /* FY_HEAP_H_ */
