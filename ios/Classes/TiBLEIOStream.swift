/**
 * Appcelerator Titanium Mobile - Bluetooth Low Energy (BLE) Module
 * Copyright (c) 2020 by Axway, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

import UIKit

protocol IOStreamListener: class {
    func onDataReceived(data: Data)
    func onStreamError(error: Error)
    func onStreamEndEncountered()
}

class TiBLEIOStream: NSObject {
    var bufLength = 1024

    private var queueQueue = DispatchQueue(label: "BLE Channel Queue", qos: .userInitiated, attributes: [], autoreleaseFrequency: .workItem, target: nil)

    private var input: InputStream
    private var output: OutputStream
    private weak var listener: IOStreamListener!
    private var isReading = false
    private var outputData = Data()

    init(inputStream: InputStream, outputStream: OutputStream, streamListener: IOStreamListener) {
        input = inputStream
        output = outputStream
        listener = streamListener
        super.init()
        outputStream.delegate = self
        inputStream.delegate = self
        inputStream.schedule(in: RunLoop.main, forMode: .default)
        outputStream.schedule(in: RunLoop.main, forMode: .default)
        inputStream.open()
        outputStream.open()
    }

    func write(data: Data) {
        queueQueue.sync {
            self.outputData.append(data)
        }
        self.send()
    }

    func close() {
        input.close()
        output.close()
    }

    private func send() {
        let ostream = self.output
        guard !self.outputData.isEmpty, ostream.hasSpaceAvailable else {
            return
        }
        let bytesWritten =  ostream.write(data: self.outputData)
        print("bytesWritten = \(bytesWritten)")
        queueQueue.sync {
            if bytesWritten < outputData.count {
                outputData = outputData.advanced(by: bytesWritten)
            } else {
                outputData.removeAll()
            }
        }
    }
    private func readBytes(from stream: InputStream) {
        let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: bufLength)
        defer {
            buffer.deallocate()
        }
        let bytesRead = stream.read(buffer, maxLength: bufLength)
        if bytesRead != 0 {
            var returnData = Data()
            returnData.append(buffer, count: bytesRead)
            self.listener.onDataReceived(data: returnData)
        }
        if stream.hasBytesAvailable {
            self.readBytes(from: stream)
        }
    }
}

extension TiBLEIOStream: StreamDelegate {
    func stream(_ aStream: Stream, handle eventCode: Stream.Event) {
        switch eventCode {
        case Stream.Event.openCompleted:
            print("Stream is open")
        case Stream.Event.endEncountered:
            print("End Encountered")
            listener.onStreamEndEncountered()
        case Stream.Event.hasBytesAvailable:
            print("Bytes are available")
            if let stream = aStream as? InputStream {
                self.readBytes(from: stream)
            }
        case Stream.Event.hasSpaceAvailable:
            print("Space is available")
            self.send()
        case Stream.Event.errorOccurred:
            print("Stream error")
            if let error = aStream.streamError {
                listener.onStreamError(error: error)
            }
        default:
            print("Unknown stream event")
        }
    }
}

extension OutputStream {
    @discardableResult
    func write(data: Data) -> Int {
        return data.withUnsafeBytes {
            write($0.bindMemory(to: UInt8.self).baseAddress!, maxLength: data.count)
        }
    }
}
