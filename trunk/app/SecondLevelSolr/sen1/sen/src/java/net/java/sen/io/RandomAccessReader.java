/*
 * RandomAccessReader.java - read file by RandomAccessFile.
 * 
 * Copyright (C) 2002 Takashi Okamoto Takashi Okamoto <tora@debian.org>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Sen; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 *  
 */

package net.java.sen.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessReader extends RandomAccessFile
    implements FileAccessor {
  
  public RandomAccessReader(String name) throws IOException {
    super(name, "r");
  }

  public RandomAccessReader(File file) throws IOException {
    super(file, "r");
  }
  public void seek(int pos) throws IOException {
    super.seek((long) pos);
  }
}