db = db.getSiblingDB(process.env.MONGO_INITDB_DATABASE);

// Create the collection
db.createCollection('parsing-history', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['parsing_hash', 'parsed_vacancies', 'new_vacancies', 'finished', 'parsers'],
      properties: {
        parsing_hash: {
          bsonType: 'string'
        },
        parsed_vacancies: {
          bsonType: 'int'
        },
        new_vacancies: {
          bsonType: 'int'
        },
        finished: {
          bsonType: 'bool'
        },
        parsers: {
          bsonType: 'array',
          items: {
            bsonType: 'object',
            required: ['parser_id', 'company_title', 'total_pages', 'metadata_status', 'parsed_vacancies', 'new_vacancies', 'pages'],
            properties: {
              parser_id: {
                bsonType: 'int'
              },
              company_title: {
                bsonType: 'string'
              },
              total_pages: {
                bsonType: 'int'
              },
              metadata_status: {
                enum: ['pending', 'done', 'error']
              },
              parsed_vacancies: {
                bsonType: 'int'
              },
              new_vacancies: {
                bsonType: 'int'
              },
              pages: {
                bsonType: 'array',
                items: {
                  bsonType: 'object',
                  required: ['id', 'status', 'parsed_vacancies', 'logs'],
                  properties: {
                    id: {
                      bsonType: 'int'
                    },
                    status: {
                      enum: ['pending', 'done', 'error']
                    },
                    parsed_vacancies: {
                      bsonType: 'int'
                    },
                    logs: {
                      bsonType: 'array',
                      items: {
                        bsonType: 'object',
                        required: ['page_id', 'level', 'timestamp', 'message'],
                        properties: {
                          page_id: {
                            bsonType: 'int'
                          },
                          level: {
                            bsonType: 'string'
                          },
                          timestamp: {
                            bsonType: 'date'
                          },
                          message: {
                            bsonType: 'string'
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
});

// Create indexes if needed
db.parsing_sessions.createIndex({ "parsing_hash": 1 }, { unique: true });
db.parsing_sessions.createIndex({ "parsers.parser_id": 1 });