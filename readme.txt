-jetty 12.0.x
-web socket แบบไม่ใช้ annotaion ทำแบบ programmatic เพื่อให้รองรับ graalVM
-appResources directory ต้องอยู่ข้างนอก ช่วยเรื่องตอนใช้ graalVM
-logback.xml อยู่ข้างในได้เพราะ realonly file แต่ห้ามใช้ option scanfile
 และเพิ่ม resource-config.json ตอนใช้ graalVM
 หากใช้ scanfile ต้องเอาออกมาข้างนอกแบบ appResources directory
 
 
 ตัวอย่าง resource-config.json
 {
  "resources": {
    "includes": [
      { "pattern": "appResources/.*" }
    ]
  }
}

ตัวอย่าง reflection-config.json
[
  {
    "name": "com.example.zk.Index",   <-- class ที่ zul อ้างอิงด้วยคำสั่ง apply
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true,
    "allDeclaredFields": true
  }
]
หรืออย่างน้อย
[
  {
    "name": "com.example.zk.Index",
    "constructors": [
      { "parameterTypes": [] }
    ]
  }
]


เหตุผล:
Native image ไม่มี real URL filesystem
classpath resource ไม่ map เป็น directory จริง
Jetty DefaultServlet ต้องการ resource ที่ list ได้
