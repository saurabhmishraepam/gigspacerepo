# VerizonMainFrame

Folder 'final table scripts 1703' contains queries for creating DB2 tables with indexes for Page 1703
initialLoad contains PU, which does:
 - initial load of embedded space with data from DB2;
 - processing the data (doing join operation) fro all possible variants;
 - saving final objects, that can be used later for real-time query, to space.