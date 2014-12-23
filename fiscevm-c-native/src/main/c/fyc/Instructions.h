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

#ifndef INSTRUCTIONS_H_
#define INSTRUCTIONS_H_

#include "../fiscestu.h"

#define FY_OP_nop  0x00
#define FY_OP_aconst_null  0x01
#define FY_OP_iconst_m1  0x02
#define FY_OP_iconst_0  0x03
#define FY_OP_iconst_1  0x04
#define FY_OP_iconst_2  0x05
#define FY_OP_iconst_3  0x06
#define FY_OP_iconst_4  0x07

#define FY_OP_iconst_5  0x08
#define FY_OP_lconst_0  0x09
#define FY_OP_lconst_1  0x0a
#define FY_OP_fconst_0  0x0b
#define FY_OP_fconst_1  0x0c
#define FY_OP_fconst_2  0x0d
#define FY_OP_dconst_0  0x0e
#define FY_OP_dconst_1  0x0f

#define FY_OP_bipush  0x10
#define FY_OP_sipush  0x11
#define FY_OP_ldc  0x12
#define FY_OP_ldc_w  0x13
#define FY_OP_ldc2_w  0x14
#define FY_OP_iload  0x15
#define FY_OP_lload  0x16
#define FY_OP_fload  0x17

#define FY_OP_dload  0x18
#define FY_OP_aload  0x19
#define FY_OP_iload_0  0x1a
#define FY_OP_iload_1  0x1b
#define FY_OP_iload_2  0x1c
#define FY_OP_iload_3  0x1d
#define FY_OP_lload_0  0x1e
#define FY_OP_lload_1  0x1f

#define FY_OP_lload_2  0x20
#define FY_OP_lload_3  0x21
#define FY_OP_fload_0  0x22
#define FY_OP_fload_1  0x23
#define FY_OP_fload_2  0x24
#define FY_OP_fload_3  0x25
#define FY_OP_dload_0  0x26
#define FY_OP_dload_1  0x27

#define FY_OP_dload_2  0x28
#define FY_OP_dload_3  0x29
#define FY_OP_aload_0  0x2a
#define FY_OP_aload_1  0x2b
#define FY_OP_aload_2  0x2c
#define FY_OP_aload_3  0x2d
#define FY_OP_iaload  0x2e
#define FY_OP_laload  0x2f

#define FY_OP_faload  0x30
#define FY_OP_daload  0x31
#define FY_OP_aaload  0x32
#define FY_OP_baload  0x33
#define FY_OP_caload  0x34
#define FY_OP_saload  0x35
#define FY_OP_istore  0x36
#define FY_OP_lstore  0x37

#define FY_OP_fstore  0x38
#define FY_OP_dstore  0x39
#define FY_OP_astore  0x3a
#define FY_OP_istore_0  0x3b
#define FY_OP_istore_1  0x3c
#define FY_OP_istore_2  0x3d
#define FY_OP_istore_3  0x3e
#define FY_OP_lstore_0  0x3f

#define FY_OP_lstore_1  0x40
#define FY_OP_lstore_2  0x41
#define FY_OP_lstore_3  0x42
#define FY_OP_fstore_0  0x43
#define FY_OP_fstore_1  0x44
#define FY_OP_fstore_2  0x45
#define FY_OP_fstore_3  0x46
#define FY_OP_dstore_0  0x47

#define FY_OP_dstore_1  0x48
#define FY_OP_dstore_2  0x49
#define FY_OP_dstore_3  0x4a
#define FY_OP_astore_0  0x4b
#define FY_OP_astore_1  0x4c
#define FY_OP_astore_2  0x4d
#define FY_OP_astore_3  0x4e
#define FY_OP_iastore  0x4f

#define FY_OP_lastore  0x50
#define FY_OP_fastore  0x51
#define FY_OP_dastore  0x52
#define FY_OP_aastore  0x53
#define FY_OP_bastore  0x54
#define FY_OP_castore  0x55
#define FY_OP_sastore  0x56
#define FY_OP_pop  0x57

#define FY_OP_pop2  0x58
#define FY_OP_dup  0x59
#define FY_OP_dup_x1  0x5a
#define FY_OP_dup_x2  0x5b
#define FY_OP_dup2  0x5c
#define FY_OP_dup2_x1  0x5d
#define FY_OP_dup2_x2  0x5e
#define FY_OP_swap  0x5f

#define FY_OP_iadd  0x60
#define FY_OP_ladd  0x61
#define FY_OP_fadd  0x62
#define FY_OP_dadd  0x63
#define FY_OP_isub  0x64
#define FY_OP_lsub  0x65
#define FY_OP_fsub  0x66
#define FY_OP_dsub  0x67

#define FY_OP_imul  0x68
#define FY_OP_lmul  0x69
#define FY_OP_fmul  0x6a
#define FY_OP_dmul  0x6b
#define FY_OP_idiv  0x6c
#define FY_OP_ldiv  0x6d
#define FY_OP_fdiv  0x6e
#define FY_OP_ddiv  0x6f

#define FY_OP_irem  0x70
#define FY_OP_lrem  0x71
#define FY_OP_frem  0x72
#define FY_OP_drem  0x73
#define FY_OP_ineg  0x74
#define FY_OP_lneg  0x75
#define FY_OP_fneg  0x76
#define FY_OP_dneg  0x77

#define FY_OP_ishl  0x78
#define FY_OP_lshl  0x79
#define FY_OP_ishr  0x7a
#define FY_OP_lshr  0x7b
#define FY_OP_iushr  0x7c
#define FY_OP_lushr  0x7d
#define FY_OP_iand  0x7e
#define FY_OP_land  0x7f

#define FY_OP_ior  0x80
#define FY_OP_lor  0x81
#define FY_OP_ixor  0x82
#define FY_OP_lxor  0x83
#define FY_OP_iinc  0x84
#define FY_OP_i2l  0x85
#define FY_OP_i2f  0x86
#define FY_OP_i2d  0x87

#define FY_OP_l2i  0x88
#define FY_OP_l2f  0x89
#define FY_OP_l2d  0x8a
#define FY_OP_f2i  0x8b
#define FY_OP_f2l  0x8c
#define FY_OP_f2d  0x8d
#define FY_OP_d2i  0x8e
#define FY_OP_d2l  0x8f

#define FY_OP_d2f  0x90
#define FY_OP_i2b  0x91
#define FY_OP_i2c  0x92
#define FY_OP_i2s  0x93
#define FY_OP_lcmp  0x94
#define FY_OP_fcmpl  0x95
#define FY_OP_fcmpg  0x96
#define FY_OP_dcmpl  0x97

#define FY_OP_dcmpg  0x98
#define FY_OP_ifeq  0x99
#define FY_OP_ifne  0x9a
#define FY_OP_iflt  0x9b
#define FY_OP_ifge  0x9c
#define FY_OP_ifgt  0x9d
#define FY_OP_ifle  0x9e
#define FY_OP_if_icmpeq  0x9f

#define FY_OP_if_icmpne  0xa0
#define FY_OP_if_icmplt  0xa1
#define FY_OP_if_icmpge  0xa2
#define FY_OP_if_icmpgt  0xa3
#define FY_OP_if_icmple  0xa4
#define FY_OP_if_acmpeq  0xa5
#define FY_OP_if_acmpne  0xa6
#define FY_OP_goto  0xa7

#define FY_OP_jsr  0xa8
#define FY_OP_ret  0xa9
#define FY_OP_tableswitch  0xaa
#define FY_OP_lookupswitch  0xab
#define FY_OP_ireturn  0xac
#define FY_OP_lreturn  0xad
#define FY_OP_freturn  0xae
#define FY_OP_dreturn  0xaf

#define FY_OP_areturn  0xb0
#define FY_OP_return  0xb1
#define FY_OP_getstatic  0xb2
#define FY_OP_putstatic  0xb3
#define FY_OP_getfield  0xb4
#define FY_OP_putfield  0xb5
#define FY_OP_invokevirtual  0xb6
#define FY_OP_invokespecial  0xb7

#define FY_OP_invokestatic  0xb8
#define FY_OP_invokeinterface  0xb9
#define FY_OP_unused_ba  0xba
#define FY_OP_new  0xbb
#define FY_OP_newarray  0xbc
#define FY_OP_anewarray  0xbd
#define FY_OP_arraylength  0xbe
#define FY_OP_athrow  0xbf

#define FY_OP_checkcast  0xc0
#define FY_OP_instanceof  0xc1
#define FY_OP_monitorenter  0xc2
#define FY_OP_monitorexit  0xc3
#define FY_OP_wide  0xc4
#define FY_OP_multianewarray  0xc5
#define FY_OP_ifnull  0xc6
#define FY_OP_ifnonnull  0xc7

#define FY_OP_goto_w  0xc8
#define FY_OP_jsr_w  0xc9
#define FY_OP_breakpoint  0xca

#define FY_OP_getstatic_x  0xf2
#define FY_OP_putstatic_x  0xf3
#define FY_OP_getfield_x  0xf4
#define FY_OP_putfield_x  0xf5

#define FY_OP_dropout  0x1f0

#define FY_IP_dropout 0x00

#define FY_IP_begin 0x01

#define fy_nextU1(CODE) (fy_uint)CODE[pc++]
#define fy_nextS1(CODE) ((fy_byte)CODE[pc++])
#define fy_nextU2(CODE) (fy_B2TOI(CODE[pc],CODE[pc+1])&0xffff);pc+=2
#define fy_nextS2(CODE) fy_B2TOI(CODE[pc],CODE[pc+1]);pc+=2
#define fy_nextS4(CODE) fy_B4TOI(CODE[pc],CODE[pc+1],CODE[pc+2],CODE[pc+3]);pc+=4

#ifdef	__cplusplus
extern "C" {
#endif
extern const char *FY_OP_NAME[512];

void fy_instInitStackItem(fy_memblock *block, fy_instruction *instruction,
		fy_int size, fy_exception *exception) ;
void fy_instStackItemClone(fy_instruction *from, fy_instruction *to);
void fy_instMarkStackItem(fy_instruction *instruction, fy_int pos,
		fy_long isHandle);
fy_int fy_instGetStackItem(fy_instruction *instruction, fy_int pos);
#ifdef	__cplusplus
}
#endif
#endif /* INSTRUCTIONS_H_ */
