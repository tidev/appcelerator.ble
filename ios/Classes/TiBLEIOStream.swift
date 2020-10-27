//
//  TiBLEIOStream.swift
//  AppceleratorBle
//
//  Created by Vikas Goyal on 26/10/20.
//

import UIKit

protocol IOStreamListener: class {
    func onDataReceived(data: Data)
    func onStreamError(error: Error)
}

class TiBLEIOStream: NSObject {
    private var input: InputStream
    private var output: OutputStream
    private weak var listener: IOStreamListener!

    init(inputStream: InputStream, outputStream: OutputStream, streamListener: IOStreamListener) {
        input = inputStream
        output = outputStream
        listener = streamListener
    }

    func write(data: Data) {
        listener.onDataReceived(data: "Dummy Read Data".data(using: String.Encoding.utf8) ?? Data())
    }
}
