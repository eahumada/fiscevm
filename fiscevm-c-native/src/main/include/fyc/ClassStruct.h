/**
 *  Copyright 2010-2013 Yuxuan Huang. All rights reserved.
 *
 * This file is part of fiscevm
 *
 * fiscevm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * fiscevm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with fiscevm  If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef FY_CLASS_STRUCT_H
#define	FY_CLASS_STRUCT_H

#include "fisce.h"
#include "fy_util/String.h"
#include "fy_util/HashMapI.h"
#include "fy_util/ArrList.h"

typedef struct fy_engine fy_engine;
typedef struct fy_instruction fy_instruction;
typedef struct fy_instruction_extra fy_instruction_extra;
typedef struct fy_thread fy_thread;

typedef struct ConstantClass ConstantClass;
typedef struct ConstantNameAndTypeInfo ConstantNameAndTypeInfo;
typedef struct fy_class fy_class;
typedef struct fy_method fy_method;
typedef struct fy_engine fy_engine;
typedef struct fy_object_data fy_object_data;

typedef fy_int (*fy_nhFunction)(
		fy_context *context,
		fy_thread *thread,
		void *data,
		fy_stack_item *args,
		fy_int argsCount,
		fy_int ops,
		fy_exception *exception
);

typedef struct fy_nh {
	void *data;
	fy_nhFunction handler;
}fy_nh;

typedef union stringInfo {
	fy_char string_index;
	fy_str* string;
	fy_int handle;
}stringInfo;

typedef union classInfo {
	fy_char name_index;
	fy_str* className;
	ConstantClass *constantClass;
	fy_class *clazz;
}classInfo;

typedef struct ConstantClass {

	fy_ubyte derefed;

	classInfo ci;
}ConstantClass;

typedef struct ConstantFieldRef {

	fy_ubyte derefed;

	union {
		fy_char class_index;
		ConstantClass *constantClass;
		struct fy_class *clazz;
	};
	union {
		fy_char name_type_index;
		ConstantNameAndTypeInfo *constantNameType;
		fy_str* nameType;
	};
	struct fy_field *field;
}ConstantFieldRef;

typedef struct ConstantMethodRef {

	fy_ubyte derefed;

	fy_char class_index;
	ConstantClass *constantClass;
	fy_class *clazz;

	fy_char name_type_index;
	ConstantNameAndTypeInfo *constantNameType;
	fy_str* nameType;

	/*The orignal method, not overridden.*/
	fy_method *method;
}ConstantMethodRef;

typedef struct ConstantStringInfo {

	fy_ubyte derefed;

	stringInfo ci;
}ConstantStringInfo;

typedef struct ConstantIntegerFloatInfo {
	fy_int value;
}ConstantIntegerFloatInfo;

typedef struct ConstantLongDoubleInfo {
	fy_long value;
}ConstantLongDoubleInfo;

typedef struct ConstantNameAndTypeInfo {

	union {
		fy_char name_index;
		fy_str* name;
	};

	union {
		fy_char descriptor_index;
		fy_str* descriptor;
	};
}ConstantNameAndTypeInfo;

typedef struct ConstantUtf8Info {
	fy_str* string;
}ConstantUtf8Info;

typedef struct fy_field {
	fy_uint field_id;
	fy_char access_flags;

	/*fy_char name_index;*/
	char* utf8Name;
	fy_str* name;

	/*fy_char descriptor_index;*/
	fy_str* descriptor;

	fy_char constant_value_index;

	fy_str* fullName;
	fy_str* uniqueName;

	fy_class* owner;
	fy_class *type;

	fy_uint posRel;
	fy_uint posAbs;

}fy_field;

typedef struct fy_lineNumber {
	fy_char start_pc;
	fy_char line_number;
}fy_lineNumber;

typedef struct fy_exceptionHandler {
	fy_char start_pc;
	fy_char end_pc;
	fy_char handler_pc;
	fy_ubyte catchTypeDerefed;

	classInfo ci;
}fy_exceptionHandler;

typedef struct fy_stack_map_table {
	fy_uint length;
	fy_int count;
	FY_VLS(fy_ubyte, entries);
}fy_stack_map_table;

typedef struct fy_method {
	fy_int method_id;
	fy_uint access_flags;

	char* utf8Name;
	fy_str* name;

	fy_str* descriptor;

	fy_str* fullName;
	fy_str* uniqueName;

	fy_class* owner;

	fy_char max_stack;
	fy_char max_locals;

	fy_uint codeLength;
	union {
		fy_ubyte *code;
		struct{
			fy_instruction *instructions;
			fy_instruction_extra *instruction_extras;
			fy_short *instruction_ops;
		};
		fy_nh *nh;
	};
	fy_stack_map_table *stackMapTable;
	fy_char exception_table_length;
	fy_exceptionHandler *exception_table;

	fy_char line_number_table_length;
	fy_lineNumber* line_number_table;

	/*The count of the parameters (long/double will be counted as 2)*/
	fy_int paramStackUsage;
	fy_byte *paramTypes;
	fy_byte returnType;

	fy_boolean clinit;

	/*Used by reflection, contents refrences of class*/
	/*Will not be saved in save-status, as it will be re-initialized when the class is reloaded */
	/*real count parameters (long/double will be counted as 1)*/
	fy_uint parameterCount;
	fy_arrayList* parameterTypes;
	fy_class *returnTypeClass;
	fy_engine *engine;
}fy_method;

typedef enum fy_arrayType {
	fy_at_byte, fy_at_short, fy_at_int, fy_at_long
}fy_arrayType;

typedef struct fy_class {
	/*        _u4 magic;
	 //        _u2 minorVersion;
	 //        _u2 majorVersion;
	 //fields from file
	 *
	 */
	enum type {
		object_class, primitive_class, array_class
	}type;
	fy_ubyte phase;
	fy_ubyte needFinalize;
	fy_char constantPoolCount;
	fy_ubyte *constantTypes;
	void** constantPools;

	fy_uint accessFlags;
	ConstantClass* thisClass;
	char* utf8Name;
	fy_str* className;
	union {
		ConstantClass* superClass;
		struct fy_class* super;
	};
	fy_char interfacesCount;
	ConstantClass** interfaceClasses;
	fy_class** interfaces;
	fy_char fieldCount;
	fy_field** fields;
	/*BEGIN GC Only*/
	fy_field **fieldStatic;
	fy_field **fieldAbs;
	/* END  GC Only*/
	fy_char methodCount;
	fy_method** methods;
	fy_method* clinit;
	fy_str* sourceFile;
	int classId;

	/*fields filled by classloader*/
	fy_uint sizeRel;
	fy_uint sizeAbs;
	fy_uint ofsInHeap;

	fy_uint staticSize;
	fy_int *staticArea;

	union {
		struct {
			fy_arrayType arrayType;
			fy_class *contentClass;
		}arr;
		struct {
			fy_char pType;
		}prm;
	}ci;

	fy_hashMapI virtualTable[1];

	/*Need persist*/
	fy_int clinitThreadId;
}fy_class;

typedef struct fy_object {
	fy_object_data *object_data;
}fy_object;

#endif
