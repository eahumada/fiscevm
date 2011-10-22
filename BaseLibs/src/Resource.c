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

#include "fyc/Resource.h"
fy_data *fy_resourceAllocateData(fy_VMContext *context, const char *name) {
	FILE *fp;
	fy_data *data;
	size_t length;
	data = fy_vmAllocate(context, sizeof(fy_data));
	fp = fopen(name, "rb");
	if (fp == NULL) {
		return NULL;
	}
	fseek(fp, 0L, SEEK_END);
	length = ftell(fp);
	rewind(fp);
	data->data = fy_vmAllocate(context, length);
	data->size = length;
	fread(data->data, length, 1, fp);
	fclose(fp);
	return data;
}

void fy_resourceReleaseData(fy_VMContext *context, fy_data *data) {
	fy_vmFree(context, data->data);
	fy_vmFree(context, data);
}
