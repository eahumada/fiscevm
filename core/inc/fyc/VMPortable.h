/**
 *  Copyright 2010-2011 Yuxuan Huang. All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef VMPORTABLE_H_
#define VMPORTABLE_H_

#include "Structs.h"

#ifdef	__cplusplus
extern "C" {
#endif

void fy_portInit(fy_VMContext *context);
void fy_portDestroy(fy_VMContext *context);
jlong fy_portTimeMillSec(fy_VMContext *context);
jlong fy_portTimeNanoSec(fy_VMContext *context);

#ifdef	__cplusplus
}
#endif

#endif /* VMPORTABLE_H_ */