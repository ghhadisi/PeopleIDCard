#!/bin/bash

#编译https://github.com/tesseract-ocr/tesseract/wiki/Compiling


# mac上出现一个编译问题:
# 无法找到PKG_CHECK_MODULES宏。
# http://codica.pl/2008/08/31/problem-with-pkg_check_modules-under-mac-os-x/
# PKG_CHECK_MODULES宏定义在pkg-config的.m4中
# 如brew 安装的目录 /usr/local/Cellar/pkg-config/0.29/share/aclocal/pkg.m4
# 但是使用autoconfig工具时候aclocal.m4没有这个宏，因为autoconfig和我安装的pkg-config不在一个prefix
# 所以运行 重新生成.m4
# aclocal -I /usr/local/share/aclocal
# autoheader
# automake
# autoconf


#CPPFLAGS and LDFLAGS can be read from brew info icu4c
#brew info icu4c
./autogen.sh
./configure CPPFLAGS=-I/usr/local/opt/icu4c/include \
 LDFLAGS=-L/usr/local/opt/icu4c/lib \
 --with-extra-libraries=/usr/local/lib \
 --with-extra-includes=/usr/local/include \
 --prefix=`pwd`/bin

make 
make install
make training
make training-install