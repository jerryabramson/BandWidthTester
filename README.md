<pre>
# BandWidthTester
Uses iperf3 asyncronously for real-time performance measurements at the command-line

EXAMPLE USAGE
$ java -jar BandWidthTester-1.0-SNAPSHOT.jar localhost
Executing command /usr/local/bin/iperf3 --forceflush --connect-timeout 3000 -c localhost -O 2 -P 8 -t 10 :  ├──┤
Connecting to host localhost, port 5201
  Local Host/IP: ::1 remote Host/IP: ::1 Remote Port: 5201
  ──────────────────────────────────────────────────────────────────────────────
             0.00-1.00                      80.1  Gbits/sec (omitted)
      Running  5.00-6.00   + [         ]   48.4  Gbits/sec

...

Executing command /usr/local/bin/iperf3 --forceflush --connect-timeout 3000 -c localhost -O 2 -P 8 -t 10 :  ├──┤
Connecting to host localhost, port 5201
  Local Host/IP: ::1 remote Host/IP: ::1 Remote Port: 5201
  ──────────────────────────────────────────────────────────────────────────────
             0.00-1.00                      80.1  Gbits/sec (omitted)
      Running  9.00-10.00    [          ]   48.4  Gbits/sec
  ──────────────────────────────────────────────────────────────────────────────
  Results:   0.00-10.00                     48.5  Gbits/sec sender
  Results:   0.00-10.00                     48.5  Gbits/sec receiver
Return Code: 000
</pre>
<pre>
Executing command <span style="background-color:green;">/usr/local/bin/iperf3 --forceflush --connect-timeout 3000 -c localhost -O 2 -P 8 -t 10 </span>:  ├──────┤
Connecting to host localhost, port 5201
  Local Host/IP: <span style="color:red;font-weight:bold;">::1</span> remote Host/IP: <span style="color:green;">::1</span> Remote Port: <span style="color:green;">5201</span>
  ──────────────────────────────────────────────────────────────────────────────
-/+\-/+          <span style="color:blue;font-weight:bold;">0.00-1.00   </span>   <span style="background-color:green;font-weight:bold;">  69.5</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;">(omitted)</span>
\-/   Running  <span style="color:blue;font-weight:bold;">1.00-1.00   </span>   <span style="background-color:green;font-weight:bold;">  37.7</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>+\-   Running  <span style="color:blue;font-weight:bold;">1.00-2.00   </span>   <span style="background-color:green;font-weight:bold;">  73.8</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>/+\   Running  <span style="color:blue;font-weight:bold;">2.00-3.00   </span>   <span style="background-color:green;font-weight:bold;">  76.5</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>-/+   Running  <span style="color:blue;font-weight:bold;">3.00-4.00   </span>   <span style="background-color:green;font-weight:bold;">  75.1</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>\-/   Running  <span style="color:blue;font-weight:bold;">4.00-5.00   </span>   <span style="background-color:green;font-weight:bold;">  75.7</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>+\-   Running  <span style="color:blue;font-weight:bold;">5.00-6.00   </span>   <span style="background-color:green;font-weight:bold;">  77.9</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>/+\   Running  <span style="color:blue;font-weight:bold;">6.00-7.00   </span>   <span style="background-color:green;font-weight:bold;">  75.2</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>-/+   Running  <span style="color:blue;font-weight:bold;">7.00-8.00   </span>   <span style="background-color:green;font-weight:bold;">  79.8</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>\-/   Running  <span style="color:blue;font-weight:bold;">8.00-9.00   </span>   <span style="background-color:green;font-weight:bold;">  79.2</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>   Running  <span style="color:blue;font-weight:bold;">9.00-10.00  </span>   <span style="background-color:green;font-weight:bold;">  72.4</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="color:blue;"></span>[]<span style="background-color:green;font-weight:bold;"> </span><span style="color:green;"></span>]
  ──────────────────────────────────────────────────────────────────────────────
Results:   <span style="background-color:gray;font-weight:bold;">0.00-10.00  </span>   <span style="background-color:green;font-weight:bold;">  76.1</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="background-color:blue;font-weight:bold;">sender</span>
Results:   <span style="background-color:gray;font-weight:bold;">0.00-10.00  </span>   <span style="background-color:green;font-weight:bold;">  76.1</span>  <span style="text-decoration:underline;">Gbits/sec</span> <span style="background-color:blue;font-weight:bold;">receiver</span>
Return Code: <span style="color:green;">000</span>
</pre>
</body>
</html>
