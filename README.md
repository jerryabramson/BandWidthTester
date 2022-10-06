<head>
<meta http-equiv="Content-Type" content="application/xml+xhtml; charset=UTF-8" />
<title>BandWidthTester</title>
</head>
<body>
  <H1>BandWidthTester</H1>
  <p>
    <br/>
    <LI>
      Uses iperf3 asyncronously for real-time performance measurements
      at the command-line
    </LI>
  </p>
  <H1>
    EXAMPLE USAGE
  </H1>
  <br/>
  <pre>
Executing command <span style="background-color:green;">/usr/local/bin/iperf3 --forceflush --connect-timeout 3000 -c localhost -O 2 -P 8 -t 10 </span>:  ├──────┤
Connecting to host localhost, port 5201
  Local Host/IP: <span style="color:red;font-weight:bold;">::1</span> remote Host/IP: <span style="color:green;">::1</span> Remote Port: <span style="color:green;">5201</span>
  ──────────────────────────────────────────────────────────────────────────────
<span style="color:blue;font-weight:bold;">0.00-1.00   </span><span style="background-color:green;font-weight:bold;">  69.5</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;">(omitted)</span>
Running  <span style="color:blue;font-weight:bold;">1.00-1.00   </span><span style="background-color:green;font-weight:bold;">  38.7</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>
<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>+\-   Running  <span style="color:blue;font-weight:bold;">1.00-2.00   </span>   <span style="background-color:green;font-weight:bold;">  78.7</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>/+\   Running  <span style="color:blue;font-weight:bold;">2.00-3.00   </span>   <span style="background-color:green;font-weight:bold;">  75.2</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>-/+   Running  <span style="color:blue;font-weight:bold;">3.00-4.00   </span>   <span style="background-color:green;font-weight:bold;">  70.6</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>\-/   Running  <span style="color:blue;font-weight:bold;">4.00-5.00   </span>   <span style="background-color:green;font-weight:bold;">  67.6</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>+\-   Running  <span style="color:blue;font-weight:bold;">5.00-6.00   </span>   <span style="background-color:green;font-weight:bold;">  61.1</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>/+\   Running  <span style="color:blue;font-weight:bold;">6.00-7.00   </span>   <span style="background-color:green;font-weight:bold;">  70.5</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>-/+   Running  <span style="color:blue;font-weight:bold;">7.00-8.00   </span>   <span style="background-color:green;font-weight:bold;">  71.1</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>\-/   Running  <span style="color:blue;font-weight:bold;">8.00-9.00   </span>   <span style="background-color:green;font-weight:bold;">  83.2</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>   Running  <span style="color:blue;font-weight:bold;">9.00-10.00  </span>   <span style="background-color:green;font-weight:bold;">  77.7</span>  <span style="text-decoration:underline;">Gbits/sec</span>
<span style="color:blue;"></span><span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>
  ──────────────────────────────────────────────────────────────────────────────
Results:   <span style="background-color:gray;font-weight:bold;">0.00-10.00  </span>   <span style="background-color:green;font-weight:bold;">  73.3</span>  <span style="text-decoration:underline;">Gbits/sec</span><span style="background-color:blue;font-weight:bold;">sender</span>
Results:   <span style="background-color:gray;font-weight:bold;">0.00-10.00  </span>   <span style="background-color:green;font-weight:bold;">  73.3</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="background-color:blue;font-weight:bold;">receiver</span>

Return Code: <span style="color:green;">000</span>
</pre>
</body>
