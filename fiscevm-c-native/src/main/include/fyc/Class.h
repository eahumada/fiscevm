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
#ifndef FY_CLASS_H_
#define FY_CLASS_H_

#include "fisce.h"
#include "fyc/ClassStruct.h"
#include "fyc/VMContext.h"


#ifdef	__cplusplus
extern "C" {
#endif

fy_boolean fy_classCanCastTo(fy_context *context, fy_class *this,
		fy_class *other, fy_boolean processInterface);
fy_boolean fy_classIsSuperClassOf(fy_context * context, fy_class * this,
		fy_class * other);
fy_boolean fy_classExtendsThrowable(fy_context *context, fy_class *clazz);
fy_boolean fy_classExtendsAnnotation(fy_context *context, fy_class *clazz);
fy_boolean fy_classExtendsEnum(fy_context *context, fy_class *clazz);
fy_boolean fy_classExtendsSoftRef(fy_context *context, fy_class *clazz);
fy_boolean fy_classExtendsWeakRef(fy_context *context, fy_class *clazz);
fy_boolean fy_classExtendsPhantomRef(fy_context *context, fy_class *clazz);

#ifdef	__cplusplus
}
#endif

#endif /* FY_CLASS_H_ */
