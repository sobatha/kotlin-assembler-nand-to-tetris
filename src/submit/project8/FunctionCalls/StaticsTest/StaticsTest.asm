@256
D=A
@SP
M=D
@$ret.0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@SYS.INIT
0;JMP
($ret.0)
(CLASS1.SET)
@ARG
D=M
@0
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Class1.0
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@ARG
D=M
@1
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Class1.1
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@5
D=D-A
A=D
D=M
@15
M=D
@SP
M=M-1
A=M
D=M
@ARG
D=D+M
@SP
A=M
A=M
A=D-A
M=D-A
D=A+1
@SP
M=D
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D
@15
A=M
0;JMP
(CLASS1.GET)
@Class1.0
D=M
@SP
A=M
M=D
@SP
M=M+1
@Class1.1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
@SP
A=M
D=M
@SP
M=M-1
@SP
A=M
M=M-D
@SP
M=M+1
@LCL
D=M
@5
D=D-A
A=D
D=M
@15
M=D
@SP
M=M-1
A=M
D=M
@ARG
D=D+M
@SP
A=M
A=M
A=D-A
M=D-A
D=A+1
@SP
M=D
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D
@15
A=M
0;JMP
(SYS.INIT)
@6
D=A
@SP
A=M
M=D
@SP
M=M+1
@8
D=A
@SP
A=M
M=D
@SP
M=M+1
@SYS.SYS.INIT$ret.0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@2
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@CLASS1.SET
0;JMP
(SYS.SYS.INIT$ret.0)
@SP
M=M-1
A=M
D=M
@5
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@23
D=A
@SP
A=M
M=D
@SP
M=M+1
@15
D=A
@SP
A=M
M=D
@SP
M=M+1
@SYS.SYS.INIT$ret.1
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@2
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@CLASS2.SET
0;JMP
(SYS.SYS.INIT$ret.1)
@SP
M=M-1
A=M
D=M
@5
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@SYS.SYS.INIT$ret.2
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@CLASS1.GET
0;JMP
(SYS.SYS.INIT$ret.2)
@SYS.SYS.INIT$ret.3
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@CLASS2.GET
0;JMP
(SYS.SYS.INIT$ret.3)
(SYS.SYS.INIT$END)
@SYS.SYS.INIT$END
0;JMP
(CLASS2.SET)
@ARG
D=M
@0
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Class2.0
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@ARG
D=M
@1
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Class2.1
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@5
D=D-A
A=D
D=M
@15
M=D
@SP
M=M-1
A=M
D=M
@ARG
D=D+M
@SP
A=M
A=M
A=D-A
M=D-A
D=A+1
@SP
M=D
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D
@15
A=M
0;JMP
(CLASS2.GET)
@Class2.0
D=M
@SP
A=M
M=D
@SP
M=M+1
@Class2.1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
@SP
A=M
D=M
@SP
M=M-1
@SP
A=M
M=M-D
@SP
M=M+1
@LCL
D=M
@5
D=D-A
A=D
D=M
@15
M=D
@SP
M=M-1
A=M
D=M
@ARG
D=D+M
@SP
A=M
A=M
A=D-A
M=D-A
D=A+1
@SP
M=D
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D
@15
A=M
0;JMP
@256
D=A
@SP
M=D
@$ret.0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@SYS.INIT
0;JMP
($ret.0)
(CLASS1.SET)
@ARG
D=M
@0
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Class1.0
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@ARG
D=M
@1
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Class1.1
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@5
D=D-A
A=D
D=M
@15
M=D
@SP
M=M-1
A=M
D=M
@ARG
D=D+M
@SP
A=M
A=M
A=D-A
M=D-A
D=A+1
@SP
M=D
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D
@15
A=M
0;JMP
(CLASS1.GET)
@Class1.0
D=M
@SP
A=M
M=D
@SP
M=M+1
@Class1.1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
@SP
A=M
D=M
@SP
M=M-1
@SP
A=M
M=M-D
@SP
M=M+1
@LCL
D=M
@5
D=D-A
A=D
D=M
@15
M=D
@SP
M=M-1
A=M
D=M
@ARG
D=D+M
@SP
A=M
A=M
A=D-A
M=D-A
D=A+1
@SP
M=D
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D
@15
A=M
0;JMP
(SYS.INIT)
@6
D=A
@SP
A=M
M=D
@SP
M=M+1
@8
D=A
@SP
A=M
M=D
@SP
M=M+1
@SYS.SYS.INIT$ret.0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@2
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@CLASS1.SET
0;JMP
(SYS.SYS.INIT$ret.0)
@SP
M=M-1
A=M
D=M
@5
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@23
D=A
@SP
A=M
M=D
@SP
M=M+1
@15
D=A
@SP
A=M
M=D
@SP
M=M+1
@SYS.SYS.INIT$ret.1
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@2
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@CLASS2.SET
0;JMP
(SYS.SYS.INIT$ret.1)
@SP
M=M-1
A=M
D=M
@5
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@SYS.SYS.INIT$ret.2
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@CLASS1.GET
0;JMP
(SYS.SYS.INIT$ret.2)
@SYS.SYS.INIT$ret.3
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=A
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=A
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=A
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=A
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@0
D=D-A
@ARG
M=D
@SP
D=M
@LCL
M=D
@CLASS2.GET
0;JMP
(SYS.SYS.INIT$ret.3)
(SYS.SYS.INIT$END)
@SYS.SYS.INIT$END
0;JMP
(CLASS2.SET)
@ARG
D=M
@0
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Class2.0
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@ARG
D=M
@1
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Class2.1
D=D+A
@SP
A=M
A=M
A=D-A
M=D-A
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@5
D=D-A
A=D
D=M
@15
M=D
@SP
M=M-1
A=M
D=M
@ARG
D=D+M
@SP
A=M
A=M
A=D-A
M=D-A
D=A+1
@SP
M=D
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D
@15
A=M
0;JMP
(CLASS2.GET)
@Class2.0
D=M
@SP
A=M
M=D
@SP
M=M+1
@Class2.1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
@SP
A=M
D=M
@SP
M=M-1
@SP
A=M
M=M-D
@SP
M=M+1
@LCL
D=M
@5
D=D-A
A=D
D=M
@15
M=D
@SP
M=M-1
A=M
D=M
@ARG
D=D+M
@SP
A=M
A=M
A=D-A
M=D-A
D=A+1
@SP
M=D
@LCL
AM=M-1
D=M
@THAT
M=D
@LCL
AM=M-1
D=M
@THIS
M=D
@LCL
AM=M-1
D=M
@ARG
M=D
@LCL
AM=M-1
D=M
@LCL
M=D
@15
A=M
0;JMP